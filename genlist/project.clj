(defproject genlist "0.1.0-SNAPSHOT"
  :description "tool to generate a valid adblockplus easylist"
  :url "http://github.com/r4vi/block-the-eu-cookie-shit-list"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/tools.cli "0.2.1"]
                 [clj-time "0.4.3"]]
  :main genlist.core)
