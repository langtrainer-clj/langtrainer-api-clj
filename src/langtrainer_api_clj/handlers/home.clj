(ns langtrainer-api-clj.handlers.home
  (:use [compojure.core])
  (:require [com.stuartsierra.component :as component]))

(defrecord Home [model]
  component/Lifecycle

  (start [this]
    this)
  (stop [this]
    this))

(defn new-home-handler []
  (map->Home {}))

(defn show-fn [this]
  (fn [] (ring.util.response/response (str "Hello " (:model this)))))

(defn new-home-routes [this]
  (defroutes home-routes
    (GET "/" [] ((show-fn this)))))
