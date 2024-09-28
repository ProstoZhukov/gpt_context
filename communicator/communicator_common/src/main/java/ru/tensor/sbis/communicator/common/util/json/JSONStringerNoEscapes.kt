package ru.tensor.sbis.communicator.common.util.json

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Вернуть JSONObject в виде строки, без эскейп символов.
 */
fun JSONObject.stringify(): String {
    return JSONStringerNoEscapes().also { it.writeObject(this) }.toString()
}

/**
 * Версия [org.json.JSONStringer] без строгого следования RFC 4627,
 * который требует эскейп последовательности к определенным символам.
 *
 * Нужно для передачи данных автотестам.
 */
internal class JSONStringerNoEscapes {

    /** The output data, containing at most one top-level array or object.  */
    val out = StringBuilder()

    /**
     * Lexical scoping elements within this stringer, necessary to insert the
     * appropriate separator characters (ie. commas and colons) and to detect
     * nesting errors.
     */
    enum class Scope {
        /**
         * An array with no elements requires no separators or newlines before
         * it is closed.
         */
        EMPTY_ARRAY,

        /**
         * A array with at least one value requires a comma and newline before
         * the next element.
         */
        NONEMPTY_ARRAY,

        /**
         * An object with no keys or values requires no separators or newlines
         * before it is closed.
         */
        EMPTY_OBJECT,

        /**
         * An object whose most recent element is a key. The next element must
         * be a value.
         */
        DANGLING_KEY,

        /**
         * An object with at least one name/value pair requires a comma and
         * newline before the next element.
         */
        NONEMPTY_OBJECT,

        /**
         * A special bracket less array needed by JSONStringer.join() and
         * JSONObject.quote() only. Not used for JSON encoding.
         */
        NULL
    }

    /**
     * Unlike the original implementation, this stack isn't limited to 20
     * levels of nesting.
     */
    private val stack: MutableList<Scope> = ArrayList()

    /**
     * A string containing a full set of spaces for a single level of
     * indentation, or null for no pretty printing.
     */
    private val indent: String?

    init {
        indent = null
    }

    /**
     * Begins encoding a new array. Each call to this method must be paired with
     * a call to [.endArray].
     *
     * @return this stringer.
     */
    @Throws(JSONException::class)
    fun array(): JSONStringerNoEscapes {
        return open(Scope.EMPTY_ARRAY, "[")
    }

    /**
     * Ends encoding the current array.
     *
     * @return this stringer.
     */
    @Throws(JSONException::class)
    fun endArray(): JSONStringerNoEscapes {
        return close(Scope.EMPTY_ARRAY, Scope.NONEMPTY_ARRAY, "]")
    }

    /**
     * Begins encoding a new object. Each call to this method must be paired
     * with a call to [.endObject].
     *
     * @return this stringer.
     */
    @Throws(JSONException::class)
    fun beginObject(): JSONStringerNoEscapes {
        return open(Scope.EMPTY_OBJECT, "{")
    }

    /**
     * Ends encoding the current object.
     *
     * @return this stringer.
     */
    @Throws(JSONException::class)
    fun endObject(): JSONStringerNoEscapes {
        return close(Scope.EMPTY_OBJECT, Scope.NONEMPTY_OBJECT, "}")
    }

    /**
     * Enters a new scope by appending any necessary whitespace and the given
     * bracket.
     */
    @Throws(JSONException::class)
    fun open(empty: Scope, openBracket: String?): JSONStringerNoEscapes {
        if (stack.isEmpty() && out.isNotEmpty()) {
            throw JSONException("Nesting problem: multiple top-level roots")
        }
        beforeValue()
        stack.add(empty)
        out.append(openBracket)
        return this
    }

    /**
     * Closes the current scope by appending any necessary whitespace and the
     * given bracket.
     */
    @Throws(JSONException::class)
    fun close(empty: Scope, nonempty: Scope, closeBracket: String?): JSONStringerNoEscapes {
        val context = peek()
        if (context != nonempty && context != empty) {
            throw JSONException("Nesting problem")
        }
        stack.removeAt(stack.size - 1)
        if (context == nonempty) {
            newline()
        }
        out.append(closeBracket)
        return this
    }

    /**
     * Returns the value on the top of the stack.
     */
    @Throws(JSONException::class)
    private fun peek(): Scope {
        if (stack.isEmpty()) {
            throw JSONException("Nesting problem")
        }
        return stack[stack.size - 1]
    }

    /**
     * Replace the value on the top of the stack with the given value.
     */
    private fun replaceTop(topOfStack: Scope) {
        stack[stack.size - 1] = topOfStack
    }

    /**
     * Encodes `value`.
     *
     * @param value a [JSONObject], [JSONArray], String, Boolean,
     * Integer, Long, Double or null. May not be [NaNs][Double.isNaN]
     * or [infinities][Double.isInfinite].
     * @return this stringer.
     */
    @Throws(JSONException::class)
    fun value(value: Any?): JSONStringerNoEscapes {
        if (stack.isEmpty()) {
            throw JSONException("Nesting problem")
        }
        if (value is JSONArray) {
            writeArray(value)
            return this
        } else if (value is JSONObject) {
            writeObject(value)
            return this
        }
        beforeValue()
        if (value == null || value is Boolean
            || value === JSONObject.NULL) {
            out.append(value)
        } else if (value is Number) {
            out.append(JSONObject.numberToString(value))
        } else {
            string(value.toString())
        }
        return this
    }

    @Throws(JSONException::class)
    fun writeArray(jsonArray: JSONArray) {
        array()
        for (i in 0 until jsonArray.length()) {
            val `object` = jsonArray.opt(i)
            `object`?.let { value(it) }
        }
        endArray()
    }

    @Throws(JSONException::class)
    fun writeObject(jsonObject: JSONObject) {
        beginObject()
        val keys = jsonObject.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            key(key).value(jsonObject[key])
        }
        endObject()
    }

    /**
     * Encodes `value` to this stringer.
     *
     * @return this stringer.
     */
    @Throws(JSONException::class)
    fun value(value: Boolean): JSONStringerNoEscapes {
        if (stack.isEmpty()) {
            throw JSONException("Nesting problem")
        }
        beforeValue()
        out.append(value)
        return this
    }

    /**
     * Encodes `value` to this stringer.
     *
     * @param value a finite value. May not be [NaNs][Double.isNaN] or
     * [infinities][Double.isInfinite].
     * @return this stringer.
     */
    @Throws(JSONException::class)
    fun value(value: Double): JSONStringerNoEscapes {
        if (stack.isEmpty()) {
            throw JSONException("Nesting problem")
        }
        beforeValue()
        out.append(JSONObject.numberToString(value))
        return this
    }

    /**
     * Encodes `value` to this stringer.
     *
     * @return this stringer.
     */
    @Throws(JSONException::class)
    fun value(value: Long): JSONStringerNoEscapes {
        if (stack.isEmpty()) {
            throw JSONException("Nesting problem")
        }
        beforeValue()
        out.append(value)
        return this
    }

    private fun string(value: String) {
        out.append("\"")
        var i = 0
        val length = value.length
        while (i < length) {
            when (val c = value[i]) {
                '"' -> out.append('\\').append(c)
                else -> if (c.code <= 0x1F) {
                    out.append(String.format("\\u%04x", c.code))
                } else {
                    out.append(c)
                }
            }
            i++
        }
        out.append("\"")
    }

    private fun newline() {
        if (indent == null) {
            return
        }
        out.append("\n")
        for (i in stack.indices) {
            out.append(indent)
        }
    }

    /**
     * Encodes the key (property name) to this stringer.
     *
     * @param name the name of the forthcoming value. May not be null.
     * @return this stringer.
     */
    @Throws(JSONException::class)
    fun key(name: String?): JSONStringerNoEscapes {
        if (name == null) {
            throw JSONException("Names must be non-null")
        }
        beforeKey()
        string(name)
        return this
    }

    /**
     * Inserts any necessary separators and whitespace before a name. Also
     * adjusts the stack to expect the key's value.
     */
    @Throws(JSONException::class)
    private fun beforeKey() {
        val context = peek()
        if (context == Scope.NONEMPTY_OBJECT) { // first in object
            out.append(',')
        } else if (context != Scope.EMPTY_OBJECT) { // not in an object!
            throw JSONException("Nesting problem")
        }
        newline()
        replaceTop(Scope.DANGLING_KEY)
    }

    /**
     * Inserts any necessary separators and whitespace before a literal value,
     * inline array, or inline object. Also adjusts the stack to expect either a
     * closing bracket or another element.
     */
    @Throws(JSONException::class)
    private fun beforeValue() {
        if (stack.isEmpty()) {
            return
        }
        val context = peek()
        when {
            context == Scope.EMPTY_ARRAY    -> {
                replaceTop(Scope.NONEMPTY_ARRAY)
                newline()
            }
            context == Scope.NONEMPTY_ARRAY -> {
                out.append(',')
                newline()
            }
            context == Scope.DANGLING_KEY   -> {
                out.append(if (indent == null) ":" else ": ")
                replaceTop(Scope.NONEMPTY_OBJECT)
            }
            context != Scope.NULL           -> {
                throw JSONException("Nesting problem")
            }
        }
    }

    /**
     * Returns the encoded JSON string.
     *
     *
     * If invoked with unterminated arrays or unclosed objects, this method's
     * return value is undefined.
     *
     *
     * **Warning:** although it contradicts the general contract
     * of [Object.toString], this method returns null if the stringer
     * contains no data.
     */
    override fun toString(): String {
        return if (out.isEmpty()) "" else out.toString()
    }
}
