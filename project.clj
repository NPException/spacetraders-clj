(defproject de.npexception/spacetraders-clj "1.2.2"
  :description "A thin wrapper around the API of https://spacetraders.io/"
  :url "https://github.com/NPException/spacetraders-clj"
  :license "MIT License"
  :plugins [[lein-pprint "1.3.2"]]
  :dependencies [[org.clojure/data.json "2.4.0"]
                 #_[org.clojure/core.async "1.2.603"]
                 [com.github.ben-manes.caffeine/caffeine "3.0.3"]
                 [ring/ring-codec "1.1.2"]
                 [http-kit "2.5.3"]]
  :global-vars {*warn-on-reflection* true}
  :repl-options {:init-ns de.npexception.spacetraders-clj.core}
  :source-paths ["src"]
  :target-path "target/%s"
  :profiles {:generate-ns {:source-paths ["dev"]
                           :main token-ns-creator}
             :dev {:source-paths ["dev"]}
             :provided {:dependencies [[org.clojure/clojure "1.10.2"]]}}
  :aliases {"generate-ns" ["with-profile" "generate-ns" "run"]}
  :release-tasks [["generate-ns"]
                  ["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["vcs" "commit" "prepare release v%s"]
                  ["vcs" "tag" "v" "--no-sign"]
                  ["deploy"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit" "prepare v%s"]]
  :deploy-repositories [["clojars" {:url "https://repo.clojars.org"
                                    :username :env/clojars_username
                                    :password :env/clojars_password
                                    :sign-releases false}]
                        ["releases" :clojars]
                        ["snapshots" :clojars]])
