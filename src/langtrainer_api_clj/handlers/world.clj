(ns langtrainer-api-clj.handlers.world
  (:use [compojure.core])
  (:require [com.stuartsierra.component :as component]
            [ring.util.response :refer [response]]
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
  (fn [token]
    (let [current-user (user/fetch (:user this) token)]
      (response {:token (:token current-user)}))))

(defn new-world-routes [this]
  (routes
    (GET "/world" [token] ((show-fn this) token))))
