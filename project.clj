(defproject langtrainer-api-clj "0.1.0-SNAPSHOT"
  :description "Langtrainer API"
  :url "https://github.com/langtrainer-clj/langtrainer-api-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [postgresql/postgresql "9.3-1102.jdbc41"]
                 [compojure "1.3.1"]
                 [org.clojure/tools.logging "0.3.1"]
                 [ch.qos.logback/logback-classic "1.1.3" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.12"]
                 [org.slf4j/jcl-over-slf4j "1.7.12"]
                 [org.slf4j/log4j-over-slf4j "1.7.12"]
                 [environ "1.0.0"]
                 [ring/ring-defaults "0.1.2"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [ring/ring-json "0.4.0"]
                 [korma "0.4.2"]
                 [crypto-random "1.2.0"]
                 [com.stuartsierra/component "0.2.3"]]
  :resource-paths ["config", "resources"]
  :profiles {:dev {:aliases {"run-dev" ["trampoline" "run" "-m" "user/start"]}
                   :source-paths ["dev"]
                   :dependencies [[javax.servlet/servlet-api "2.5"]
                                  [ring-mock "0.1.5"]
                                  [org.clojure/tools.namespace "0.2.10"]]}
             :uberjar {:aot [langtrainer-api-clj.core]}}
  :main ^{:skip-aot true} langtrainer-api-clj.core
  :uberjar-name "langtrainer-api-clj.jar"
  :jvm-opts ["-server"]
  :repl-options {:init-ns user
                 :timeout 120000})
