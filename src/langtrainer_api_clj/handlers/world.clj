(ns langtrainer-api-clj.handlers.world
  (:use [compojure.core])
  (:require [ring.util.response :refer [response]]
            [langtrainer-api-clj.models.language :as language]
            [langtrainer-api-clj.models.course :as course]
            [langtrainer-api-clj.models.user :as user]))

(defn show-fn [data]
  (fn [token]
    (let [models (:models data)
          current-user (user/fetch (:user models) token)]
        (response {:token (:token current-user)
                   :languages (mapv #(select-keys % [:slug]) (language/published))
                   :courses (course/for-world models)}))))

(defn new-world-routes [data]
  (routes
    (GET "/world" [token] ((show-fn data) token))))
