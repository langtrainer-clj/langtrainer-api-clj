(ns langtrainer-api-clj.models.training
  (:use [korma.core]))

(defn new-training-model [db]
  {:entity (-> (create-entity (name "trainings"))
               (entity-fields :id))})
