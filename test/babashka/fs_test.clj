(ns babashka.fs-test
  (:require [babashka.fs :as fs]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.test :refer [deftest is testing]]
            #_[me.raynes.fs :as rfs]))

(deftest glob-test
  (is (= '("README.md") (map (comp str fs/relativize)
                             (fs/glob "." "README.md"))))
  (is (set/subset? #{"test/babashka/fs_test.clj" "src/babashka/fs.clj"}
                   (set (map (comp str fs/relativize)
                             (fs/glob "." "**/*.clj")))))
  (testing "glob also matches directories and doesn't return the root directory"
    (is (= '("test-resources/foo/1" "test-resources/foo/foo")
           (map (comp str fs/relativize)
                (fs/glob "test-resources/foo" "**")))))
  (testing "*cwd*"
    (is (= '("test-resources/foo/1" "test-resources/foo/foo")
           (map (comp str fs/relativize)
                (binding [fs/*cwd* "test-resources/foo"]
                  (fs/glob "**")))))))

(deftest file-name-test
  (is (= "fs" (fs/file-name fs/*cwd*)))
  (is (= "fs" (fs/file-name (fs/file fs/*cwd*))))
  (is (= "fs" (fs/file-name (fs/path fs/*cwd*)))))

(deftest path-test
  (let [p (fs/path "foo" "bar" (io/file "baz"))]
    (is (instance? java.nio.file.Path p))
    (is (= "foo/bar/baz" (str p)))))

(deftest file-test
  (let [f (fs/file "foo" "bar" (fs/path "baz"))]
    (is (instance? java.io.File f))
    (is (= "foo/bar/baz" (str f)))))