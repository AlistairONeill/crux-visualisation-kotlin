package main.extension

import clojure.lang.Keyword

val String.kw get() = Keyword.intern(this)