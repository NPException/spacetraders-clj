(defproject de.npexception/spacetraders-clj "0.1.0-SNAPSHOT"
  :description "A thin wrapper around the API of https://spacetraders.io/"
  :url "https://github.com/NPException/spacetraders-clj"
  :license "MIT License"
  :dependencies [[org.clojure/clojure "1.10.2"]
                 [org.clojure/data.json "2.4.0"]
                 #_[org.clojure/core.async "1.2.603"]
                 [com.github.ben-manes.caffeine/caffeine "3.0.3"]
                 [ring/ring-codec "1.1.2"]
                 [http-kit "2.5.3"]]
  :global-vars {*warn-on-reflection* true}
  :repl-options {:init-ns de.npcomplete.spacetraders-clj.core}
  :source-paths ["src"]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
