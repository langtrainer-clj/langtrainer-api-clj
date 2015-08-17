(ns langtrainer-api-clj.models.unit
  (:use [korma.core])
  (:require [langtrainer-api-clj.protocols :as protocols]
            [langtrainer-api-clj.models.utils :refer [fk]]))

(defrecord Unit [entity]
  protocols/Model

  (define-relations [this {{courses :entity} :course}]
    (assoc-in this
              [:entity :rel "course"]
              (delay
                (create-relation
                  (:entity this)
                  courses
                  :belongs-to
                  (fk :course_id))))))

(defn new-unit-model [db]
  (map->Unit {:entity
              (-> (create-entity (name "units"))
                  (entity-fields :id :slug))}))

(defn all [{units :entity} scopes]
  (apply select* (conj units scopes)))

(defn published []
   '(where {:published true}))
