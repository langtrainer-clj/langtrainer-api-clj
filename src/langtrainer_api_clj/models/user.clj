(ns langtrainer-api-clj.models.user
  (:use [korma.core :exclude [has-many]])
  (:require [crypto.random :as random]
            [langtrainer-api-clj.protocols :as protocols]
            [langtrainer-api-clj.models.utils :refer [has-many find-by]]))

(defrecord User [entity]
  protocols/HasRelations

  (define-relations [this {training :training}]
    (has-many this training "trainings" {:fk :user_id})))

(defn new-user-model [db]
  (User. (-> (create-entity "users")
             (entity-fields :token))))

(defn find-or-create-by [{users :entity :as model} token]
  (if-let [user (find-by model {:token token})]
    user
    (insert users (values {:token token}) (insert))))

(defn generate-token []
  (random/url-part 32))

(defn new-token [model]
  (loop [token (generate-token)]
    (if (find-by model {:token token})
      (recur (generate-token))
      token)))

(defn fetch [model token]
  (if-let [user (find-by model {:token token})]
    user
    {:token (generate-token)}))

(defn fetch-current-step [this user-id unit-id]
  (let [models (:models this)
        {training :training step :step} models]
    (if-let [training (find-by training {:user_id user-id :unit_id unit-id})]
      (find-by step {:id (:current_step_id training)})
      (first (select (:entity step)
                     (fields :en_answers :ru_answers :en_question :ru_question :ru_help :en_help)
                     (join :inner (get-in models [:steps-unit :entity]))
                     (where {:steps_units.unit_id unit-id})
                     (limit 1))))))
