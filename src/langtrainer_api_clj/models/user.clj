(ns langtrainer-api-clj.models.user
  (:use [korma.core])
  (:require [crypto.random :as random]
            [langtrainer-api-clj.protocols :as protocols]))

(defrecord User [entity]
  protocols/Model

  (define-closures [this models]
    (assoc
      (:user models)
      :fetch-current-step
      (fn [user-id unit-id]
        (let [{{trainings :entity} :training
               {steps :entity} :step} models]
          (println steps)
          (if-let [training
                   (first (select trainings
                                  (where {:user_id user-id})
                                  (where {:unit_id unit-id})
                                  (limit 1)))]
            (first (select steps
                           (where {:id (:current_step_id training)})
                           (limit 1)))
            (first (select steps
                           (where {:unit_id unit-id})
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
    [:token (generate-token)]))
