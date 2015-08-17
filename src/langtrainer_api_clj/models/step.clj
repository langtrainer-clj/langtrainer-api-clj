(ns langtrainer-api-clj.models.step
  (:use [korma.core]))

(defn new-step-model [db]
  {:entity (-> (create-entity (name "steps"))
               (entity-fields :id))})
