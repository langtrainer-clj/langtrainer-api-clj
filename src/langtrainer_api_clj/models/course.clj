(ns langtrainer-api-clj.models.course
  (:use [korma.core]))

(defn new-course-model [db]
  {:entity (-> (create-entity "courses")
               (database (:db db)))})

(defn published [base]
   (where base {:published true}))

(defn for-world [{{courses :entity} :course
                  {units   :entity} :unit}]
  (select courses
    (with units)
    (fields :id :slug)
    published))
