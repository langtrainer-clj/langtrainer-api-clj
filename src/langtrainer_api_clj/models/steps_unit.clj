(ns langtrainer-api-clj.models.steps-unit
  (:use [korma.core])
  (:require [langtrainer-api-clj.protocols :as protocols]
            [langtrainer-api-clj.models.utils :refer [fk]]))

(defn new-steps-unit-model [db]
  {:entity (-> (create-entity (name "steps_units"))
               (entity-fields :id))})
