(ns langtrainer-api-clj.models.step
  (:use [korma.core :exclude [has-many]])
  (:require [langtrainer-api-clj.protocols :as protocols]
            [langtrainer-api-clj.models.utils :refer [has-many]]))

(defrecord Step [entity]
  protocols/HasRelations

  (define-relations [this {steps-unit :steps-unit}]
    (has-many this steps-unit "steps_units" {:fk :step_id})))

(defn new-step-model [db]
  (Step. (-> (create-entity (name "steps"))
             (entity-fields :id))))
