(defproject spacetraders-clj-wrapper "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.2"]
                 [org.clojure/data.json "2.4.0"]
                 #_[org.clojure/core.async "1.2.603"]
                 [com.github.ben-manes.caffeine/caffeine "3.0.3"]
                 [ring/ring-codec "1.1.2"]
                 [http-kit "2.5.3"]]
  :repl-options {:init-ns de.npcomplete.spacetraders-wrapper.core}
  :source-paths ["src"]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[criterium "0.4.5"]]}})
