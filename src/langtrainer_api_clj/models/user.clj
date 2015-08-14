(ns langtrainer-api-clj.models.user
  (:use [korma.core])
  (:require [com.stuartsierra.component :as component]
            [crypto.random :as random]))

(defrecord User [db entity]
  component/Lifecycle

  (start [this]
    (if entity
      this
      (assoc this :entity
             (-> (create-entity "users")
                 (database (:db db))
                 (entity-fields :id :token)))))

  (stop [this]
    (if (not entity)
      this
      (assoc this :entity nil))))

(defn new-user-model []
  (map->User {}))

(defn find-by-token [{users :entity} token]
  (first (select users (where {:token token}) (limit 1))))

(defn find-or-create-by [{users :entity :as model} token]
  (if-let [user (find-by-token model token)]
    user
    (insert users (values {:token token}) (insert))))

(defn generate-token []
  (random/url-part 32))

(defn new-token [{users :entity :as model}]
  (loop [token (generate-token)]
    (if (find-by-token model token)
      (recur (generate-token))
      token)))

(defn fetch [{users :entity :as model} token]
  (if-let [user (find-by-token model token)]
    user
    [:token (generate-token)]))
