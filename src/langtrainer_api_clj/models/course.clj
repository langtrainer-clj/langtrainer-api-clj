(ns langtrainer-api-clj.models.course
  (:use [korma.core])
  (:require [langtrainer-api-clj.protocols :as protocols]
            [langtrainer-api-clj.models.utils :refer [fk]]))

(defrecord Course [entity]
  protocols/Model

  (define-relations [this {{units :entity} :unit}]
    (assoc-in this
              [:entity :rel "units"]
              (delay
                (create-relation
                  (:entity this)
                  units
                  :has-many
                  (fk :course_id))))))

(defn new-course-model [db]
  (map->Course {:entity
                (-> (create-entity "courses"))}))

(defn published [base]
   (where base {:published true}))

(defn for-world [{{courses :entity} :course
                  {units   :entity} :unit}]
  (select courses
    (with units)
    (fields :id :slug)
    published))
