(ns langtrainer-api-clj.models.training
  (:use [korma.core :exclude [belongs-to]])
  (:require [langtrainer-api-clj.protocols :as protocols]
            [langtrainer-api-clj.models.utils :refer [belongs-to]]))

(defrecord Training [entity]
  protocols/HasRelations

  (define-relations [this {unit :unit
                           user :user}]
    (-> this
        (belongs-to unit "unit" {:fk :unit_id})
        (belongs-to user "user" {:fk :user_id}))))

(defn new-training-model [db]
  (Training. (-> (create-entity (name "trainings"))
                 (entity-fields :id))))
