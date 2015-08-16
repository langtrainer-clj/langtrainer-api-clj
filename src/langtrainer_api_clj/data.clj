(ns langtrainer-api-clj.data
  (:require [com.stuartsierra.component :as component]
            [korma.core :refer [create-relation]]
            [langtrainer-api-clj.models.user :refer [new-user-model]]
            [langtrainer-api-clj.models.course :refer [new-course-model]]
            [langtrainer-api-clj.models.unit :refer [new-unit-model]]))

(defn fk [fk]
  "Set the foreign key used for an entity relationship."
   {:fk fk})

(defn create-relations [{{units :entity} :unit
                         {courses :entity} :course :as models}]
  (-> models
      (assoc-in [:unit :entity :rel "courses"]
                (delay (create-relation units courses :belongs-to (fk :course_id))))
      (assoc-in [:course :entity :rel "units"]
                (delay (create-relation courses units :has-many (fk :course_id))))))

(defrecord Data [db models]
  component/Lifecycle

  (start [this]
    (if models
      this
      (let [models {:user (new-user-model db)
                    :course (new-course-model db)
                    :unit (new-unit-model db)}]
        (assoc this :models (create-relations models)))))

  (stop [this]
    (if (not models) ; already stopped
      this
      (assoc this :models nil))))

(defn new-data-layer []
  (map->Data {}))
