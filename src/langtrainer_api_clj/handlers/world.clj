(ns langtrainer-api-clj.handlers.world
  (:use [compojure.core])
  (:require [ring.util.response :refer [response]]
            [langtrainer-api-clj.models.language :as language]
            [langtrainer-api-clj.models.course :as course]
            [langtrainer-api-clj.models.user :as user]))

(defn show-fn [db]
  (fn [token]
    (let [current-user (user/fetch db token)]
      (response {:token (:token current-user)
                 :languages (mapv #(select-keys % [:slug]) (language/published db))
                 :courses (course/fetch-world db (:id current-user))}))))

(defn new-world-routes [db]
  (routes
    (GET "/world" [token] ((show-fn db) token))))
