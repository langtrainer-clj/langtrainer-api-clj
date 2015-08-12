(ns langtrainer-api-clj.models.home
  (:require [com.stuartsierra.component :as component]))

(defrecord Home [db]
  component/Lifecycle

  (start [this]
    this)
  (stop [this]
    this))

(defn new-home-model []
  (map->Home {}))
