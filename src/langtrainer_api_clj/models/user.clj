(ns langtrainer-api-clj.models.user
  (:use [korma.core])
  (:require [com.stuartsierra.component :as component]))

(defrecord User [db entity]
  component/Lifecycle

  (start [this]
    (if entity
      this
      (assoc this :entity
             (-> (create-entity "users")
                 (database (:db db))
                 (entity-fields :id)))))

  (stop [this]
    (if (not entity)
      this
      (assoc this :entity nil))))

(defn new-user-model []
  (map->User {}))

(defn all [{users :entity}]
  (-> (select* users) select))
