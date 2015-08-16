(ns langtrainer-api-clj.models.user
  (:use [korma.core])
  (:require [crypto.random :as random]))

(defn new-user-model [db]
  {:entity (-> (create-entity "users")
               (database (:db db))
               (entity-fields :token))})

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
