(ns langtrainer-api-clj.models.course
  (:use [korma.core :exclude [has-many]])
  (:require [langtrainer-api-clj.protocols :as protocols]
            [langtrainer-api-clj.models.utils :refer [has-many]]))

(defrecord Course [entity]
  protocols/HasRelations

  (define-relations [this {unit :unit}]
    (has-many this unit "units" {:fk :course_id})))

(defn new-course-model [db]
  (Course. (create-entity "courses")))

(defn published [base]
   (where base {:published true}))

(defn for-world [{{courses :entity} :course
                  {units   :entity} :unit}]
  (select courses
    (with units)
    (fields :id :slug)
    published))
