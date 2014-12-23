package com.weheartit.challenge;

public class CommitModel {

    public CommitModel() {
        commit = new Commit();
    }

    public Commit commit;

    public class Commit {

        public Commit() {
            author = new Author();
        }

        public String message;
        public String id;
        public Author author;

        public class Author {
            public String name;
        }
    }
}
