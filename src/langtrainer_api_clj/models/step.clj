(ns langtrainer-api-clj.models.step
  (:use [korma.core])
  (:require [langtrainer-api-clj.protocols :as protocols]
            [langtrainer-api-clj.models.utils :refer [fk]]))

(defrecord Step [entity]
  protocols/Model

  (define-relations [this {{steps_units :entity} :steps_unit}]
    (assoc-in this
              [:entity :rel "steps_units"]
              (delay
                (create-relation
                  (:entity this)
                  steps_units
                  :has-many
                  (fk :step_id))))))

(defn new-step-model [db]
  (map->Step {:entity (-> (create-entity (name "steps"))
                          (entity-fields :id))}))
