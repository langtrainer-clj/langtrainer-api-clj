(ns langtrainer-api-clj.models.unit
  (:use [korma.core :exclude [belongs-to]])
  (:require [langtrainer-api-clj.protocols :as protocols]
            [langtrainer-api-clj.models.utils :refer [belongs-to]]))

(defrecord Unit [entity]
  protocols/HasRelations

  (define-relations [this {course :course}]
    (belongs-to this course "course" {:fk :course_id})))

(defn new-unit-model [db]
  (Unit. (-> (create-entity (name "units"))
             (entity-fields :id :slug))))
