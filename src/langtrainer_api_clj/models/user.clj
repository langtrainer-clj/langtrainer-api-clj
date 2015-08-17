(ns langtrainer-api-clj.models.user
  (:use [korma.core])
  (:require [crypto.random :as random]
            [langtrainer-api-clj.protocols :as protocols]
            [langtrainer-api-clj.models.utils :refer [fk]]))

(defrecord User [entity]
  protocols/Model

  (define-relations [this {{trainings :entity} :training}]
     (assoc-in this
               [:entity :rel "trainings"]
               (delay
                 (create-relation
                  (:entity this)
                  trainings
                  :has-many
                  (fk :user_id))))))

(extend-protocol protocols/ClosuresContainer
  User
  (define-closures [this models]
    (assoc
      this
      :fetch-current-step
      (fn [user-id unit-id]
        (let [{{trainings :entity} :training
               {steps :entity} :step
               {steps-units :entity} :steps-unit} models]
          (if-let [training
                   (first (select trainings
                                  (where {:user_id user-id})
                                  (where {:unit_id unit-id})
                                  (limit 1)))]
            (first (select steps
                           (where {:id (:current_step_id training)})
                           (limit 1)))
            (first (select steps
                           (fields :en_answers :ru_answers :en_question :ru_question :ru_help :en_help)
                           (join :inner steps-units)
                           (where {:steps_units.unit_id unit-id})
                           (limit 1)))))))))

(defn new-user-model [db]
  (map->User {:entity
              (-> (create-entity "users")
                  (entity-fields :token))}))

(defn find-by-token [{users :entity} token]
  (first (select users (where {:token token}) (limit 1))))

(defn find-or-create-by [{users :entity :as model} token]
  (if-let [user (find-by-token model token)]
    user
    (insert users (values {:token token}) (insert))))

(defn generate-token []
  (random/url-part 32))

(defn new-token [{users :entity :as model}]
  (loop [token (generate-token)]
    (if (find-by-token model token)
      (recur (generate-token))
      token)))

(defn fetch [{users :entity :as model} token]
  (if-let [user (find-by-token model token)]
    user
    {:token (generate-token)}))
