(ns langtrainer-api-clj.data
  (:require [com.stuartsierra.component :as component]
            [langtrainer-api-clj.protocols :refer [define-closures define-relations]]
            [langtrainer-api-clj.models.user :refer [new-user-model]]
            [langtrainer-api-clj.models.course :refer [new-course-model]]
            [langtrainer-api-clj.models.unit :refer [new-unit-model]]
            [langtrainer-api-clj.models.step :refer [new-step-model]]
            [langtrainer-api-clj.models.steps-unit :refer [new-steps-unit-model]]
            [langtrainer-api-clj.models.training :refer [new-training-model]]))

(defn transform-models [models f]
  (loop [model-names (keys models) models models]
    (if (seq model-names)
      (let [model-name (first model-names)]
        (recur (rest model-names) (assoc models model-name (f (model-name models) models))))
      models)))

(defrecord Data [db models]
  component/Lifecycle

  (start [this]
    (if models
      this
      (let [models {:user (new-user-model db)
                    :course (new-course-model db)
                    :unit (new-unit-model db)
                    :step (new-step-model db)
                    :steps-unit (new-steps-unit-model db)
                    :training (new-training-model db)}]

            (assoc this :models (-> models
                                    (transform-models define-relations)
                                    (transform-models define-closures))))))

  (stop [this]
    (if (not models) ; already stopped
      this
      (assoc this :models nil))))

(defn new-data-layer []
  (map->Data {}))
