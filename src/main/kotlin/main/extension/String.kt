package main.extension

import clojure.lang.Keyword

val String.kw: Keyword get() = Keyword.intern(this)