package com.nonnulldev.anotherroom.util.debug

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.Logger

class DebugCameraInfo {

    private val FILE_PATH = "debug/debugCameraInfo.json"

    // == public methods ==
    var maxZoomIn: Float = 0.toFloat()
        private set
    var maxZoomOut: Float = 0.toFloat()
        private set
    var moveSpeed: Float = 0.toFloat()
        private set
    var zoomSpeed: Float = 0.toFloat()
        private set

    private var leftKey: Int = 0
    private var rightKey: Int = 0
    private var upKey: Int = 0
    private var downKey: Int = 0

    private var zoomInKey: Int = 0
    private var zoomOutKey: Int = 0

    private var resetKey: Int = 0
    private var logKey: Int = 0

    private var fileHandle: FileHandle? = null

    init {
        init()
    }

    private fun init() {
        fileHandle = Gdx.files.internal(FILE_PATH)

        if (fileHandle!!.exists()) {
            load()
        } else {
            log.info("Using defaults file does not exist= " + FILE_PATH)
            setupDefaults()
        }
    }

    private fun load() {
        try {
            val reader = JsonReader()
            val root = reader.parse(fileHandle!!)

            maxZoomIn = root.getFloat(MAX_ZOOM_IN, DEFAULT_MAX_ZOOM_IN)
            maxZoomOut = root.getFloat(MAX_ZOOM_OUT, DEFAULT_MAX_ZOOM_OUT)
            moveSpeed = root.getFloat(MOVE_SPEED, DEFAULT_MOVE_SPEED)
            zoomSpeed = root.getFloat(ZOOM_SPEED, DEFAULT_ZOOM_SPEED)

            leftKey = getInputKeyValue(root, LEFT_KEY, DEFAULT_LEFT_KEY)
            rightKey = getInputKeyValue(root, RIGHT_KEY, DEFAULT_RIGHT_KEY)
            upKey = getInputKeyValue(root, UP_KEY, DEFAULT_UP_KEY)
            downKey = getInputKeyValue(root, DOWN_KEY, DEFAULT_DOWN_KEY)

            zoomInKey = getInputKeyValue(root, ZOOM_IN_KEY, DEFAULT_ZOOM_IN_KEY)
            zoomOutKey = getInputKeyValue(root, ZOOM_OUT_KEY, DEFAULT_ZOOM_OUT_KEY)
            resetKey = getInputKeyValue(root, RESET_KEY, DEFAULT_RESET_KEY)
            logKey = getInputKeyValue(root, LOG_KEY, DEFAULT_LOG_KEY)

        } catch (e: Exception) {
            log.error("Error loading $FILE_PATH using defaults.", e)
            setupDefaults()
        }

    }

    private fun setupDefaults() {
        maxZoomIn = DEFAULT_MAX_ZOOM_IN
        maxZoomOut = DEFAULT_MAX_ZOOM_OUT
        moveSpeed = DEFAULT_MOVE_SPEED
        zoomSpeed = DEFAULT_ZOOM_SPEED

        leftKey = DEFAULT_LEFT_KEY
        rightKey = DEFAULT_RIGHT_KEY
        upKey = DEFAULT_UP_KEY
        downKey = DEFAULT_DOWN_KEY

        zoomInKey = DEFAULT_ZOOM_IN_KEY
        zoomOutKey = DEFAULT_ZOOM_OUT_KEY
        resetKey = DEFAULT_RESET_KEY
        logKey = DEFAULT_LOG_KEY
    }

    val isLeftPressed: Boolean
        get() = Gdx.input.isKeyPressed(leftKey)

    val isRightPressed: Boolean
        get() = Gdx.input.isKeyPressed(rightKey)

    val isUpPressed: Boolean
        get() = Gdx.input.isKeyPressed(upKey)

    val isDownPressed: Boolean
        get() = Gdx.input.isKeyPressed(downKey)

    val isZoomInPressed: Boolean
        get() = Gdx.input.isKeyPressed(zoomInKey)

    val isZoomOutPressed: Boolean
        get() = Gdx.input.isKeyPressed(zoomOutKey)

    val isResetPressed: Boolean
        get() = Gdx.input.isKeyPressed(resetKey)

    val isLogPressed: Boolean
        get() = Gdx.input.isKeyPressed(logKey)

    override fun toString(): String {
        val LS = System.getProperty("line.separator")

        return "DebugCameraInfo {" + LS +
                "maxZoomIn= " + maxZoomIn + LS +
                "maxZoomOut= " + maxZoomOut + LS +
                "moveSpeed= " + moveSpeed + LS +
                "zoomSpeed= " + zoomSpeed + LS +
                "leftKey= " + Input.Keys.toString(leftKey) + LS +
                "rightKey= " + Input.Keys.toString(rightKey) + LS +
                "upKey= " + Input.Keys.toString(upKey) + LS +
                "downKey= " + Input.Keys.toString(downKey) + LS +
                "zoomInKey= " + Input.Keys.toString(zoomInKey) + LS +
                "zoomOutKey= " + Input.Keys.toString(zoomOutKey) + LS +
                "resetKey= " + Input.Keys.toString(resetKey) + LS +
                "logKey= " + Input.Keys.toString(logKey) + LS +
                "}"

    }

    companion object {

        private val log = Logger(DebugCameraInfo::class.java.name, Logger.DEBUG)

        private val MAX_ZOOM_IN = "maxZoomIn"
        private val MAX_ZOOM_OUT = "maxZoomOut"
        private val MOVE_SPEED = "moveSpeed"
        private val ZOOM_SPEED = "zoomSpeed"

        private val LEFT_KEY = "leftKey"
        private val RIGHT_KEY = "rightKey"
        private val UP_KEY = "upKey"
        private val DOWN_KEY = "downKey"

        private val ZOOM_IN_KEY = "zoomInKey"
        private val ZOOM_OUT_KEY = "zoomOutKey"
        private val RESET_KEY = "resetKey"
        private val LOG_KEY = "logKey"

        private val DEFAULT_LEFT_KEY = Input.Keys.A
        private val DEFAULT_RIGHT_KEY = Input.Keys.D
        private val DEFAULT_UP_KEY = Input.Keys.W
        private val DEFAULT_DOWN_KEY = Input.Keys.S

        private val DEFAULT_ZOOM_IN_KEY = Input.Keys.COMMA
        private val DEFAULT_ZOOM_OUT_KEY = Input.Keys.PERIOD

        private val DEFAULT_RESET_KEY = Input.Keys.BACKSPACE
        private val DEFAULT_LOG_KEY = Input.Keys.ENTER

        private val DEFAULT_MOVE_SPEED = 20.0f
        private val DEFAULT_ZOOM_SPEED = 2.0f
        private val DEFAULT_MAX_ZOOM_IN = 0.25f
        private val DEFAULT_MAX_ZOOM_OUT = 30f

        private fun getInputKeyValue(root: JsonValue, name: String, defaultInputKey: Int): Int {
            val keyString = root.getString(name, Input.Keys.toString(defaultInputKey))
            return Input.Keys.valueOf(keyString)
        }

    }
}
