(ns langtrainer-api-clj.handlers.world
  (:use [compojure.core])
  (:require [com.stuartsierra.component :as component]
            [langtrainer-api-clj.models.user :as user]))

(defrecord World [user]
  component/Lifecycle

  (start [this]
    this)
  (stop [this]
    this))

(defn new-world-handler []
  (map->World {}))

(defn show-fn [this]
  (fn []
    (ring.util.response/response (str "Hello " (user/all (:user this))))))

(defn new-world-routes [this]
  (routes
    (GET "/" [] ((show-fn this)))))
