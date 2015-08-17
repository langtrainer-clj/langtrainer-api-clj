(ns langtrainer-api-clj.data
  (:require [com.stuartsierra.component :as component]
            [langtrainer-api-clj.protocols :refer [define-closures]]
            [korma.core :refer [create-relation]]
            [langtrainer-api-clj.models.user :refer [new-user-model]]
            [langtrainer-api-clj.models.course :refer [new-course-model]]
            [langtrainer-api-clj.models.unit :refer [new-unit-model]]
            [langtrainer-api-clj.models.step :refer [new-step-model]]
            [langtrainer-api-clj.models.training :refer [new-training-model]]))

(defn fk [fk]
  "Set the foreign key used for an entity relationship."
   {:fk fk})

(defn init-relations [{{users :entity} :user
                       {units :entity} :unit
                       {courses :entity} :course
                       {steps :entity} :step
                       {trainings :entity} :training :as models}]
  (-> models
      (assoc-in [:user :entity :rel "trainings"]
                (delay (create-relation users trainings :has-many (fk :user_id))))
      (assoc-in [:unit :entity :rel "course"]
                (delay (create-relation units courses :belongs-to (fk :course_id))))
      (assoc-in [:step :entity :rel "unit"]
                (delay (create-relation steps units :belongs-to (fk :unit_id))))
      (assoc-in [:course :entity :rel "units"]
                (delay (create-relation courses units :has-many (fk :course_id))))
      (assoc-in [:training :entity :rel "user"]
                (delay (create-relation trainings users :belongs-to (fk :user_id))))
      (assoc-in [:training :entity :rel "unit"]
                (delay (create-relation trainings units :belongs-to (fk :unit_id))))))

(defn init-closures [models]
  (-> models
      (assoc :user (define-closures (:user models) models))))

(defrecord Data [db models]
  component/Lifecycle

  (start [this]
    (if models
      this
      (let [models {:user (new-user-model db)
                    :course (new-course-model db)
                    :unit (new-unit-model db)
                    :step (new-step-model db)
                    :training (new-training-model db)}]

            (assoc this :models (-> models
                                    init-relations
                                    init-closures)))))

  (stop [this]
    (if (not models) ; already stopped
      this
      (assoc this :models nil))))

(defn new-data-layer []
  (map->Data {}))
