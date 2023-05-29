package com.example.arapp

import android.app.Activity
import android.app.ActivityManager
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import java.util.Objects.requireNonNull

class MainActivity : AppCompatActivity() {
    private lateinit var arFragment: ArFragment
    private lateinit var deleteButton: Button
    private lateinit var isAddLockedCheckBox: CheckBox
    private lateinit var modelRadioGroup: RadioGroup
//    private lateinit var settingsButton: Button

    private fun checkSystemSupport(activity: Activity): Boolean {
        val openGlVersion =
            (requireNonNull(activity.getSystemService(ACTIVITY_SERVICE)) as ActivityManager)
                .deviceConfigurationInfo
                .glEsVersion
        return if (openGlVersion.toDouble() >= 3.0) {
            true
        } else {
            Toast.makeText(
                activity,
                "App needs OpenGl Version 3.0 or later",
                Toast.LENGTH_SHORT
            ).show()
            activity.finish()
            false
        }
    }

    private fun attachModelToAnchor(anchor: Anchor, model: ModelRenderable) {
        val anchorNode = AnchorNode(anchor)
        anchorNode.setParent(arFragment.arSceneView.scene)

        with(TransformableNode(arFragment.transformationSystem)) {
            setParent(anchorNode)
            renderable = model
            select()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!checkSystemSupport(this)) return

        arFragment = supportFragmentManager.findFragmentById(R.id.arCameraArea) as ArFragment
        deleteButton = findViewById(R.id.deleteButton)
        isAddLockedCheckBox = findViewById(R.id.isAddLockedCheckBox)
//        modelRadioGroup = findViewById(R.id.modelRadioGroup)
//        settingsButton = findViewById(R.id.settingsButton)

        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->
            if (!isAddLockedCheckBox.isChecked) {
//                val radioButtonId = modelRadioGroup.checkedRadioButtonId
//                val modelId = modelByRadioButton(radioButtonId)

                val anchor = hitResult.createAnchor()
                ModelRenderable.builder()
                    .setSource(this, R.raw.xbox_controller)
                    .setIsFilamentGltf(true)
                    .build()
                    .thenAccept { model ->
                        attachModelToAnchor(anchor, model)
                    }
            }
        }
        deleteButton.setOnClickListener {
            val selectedTransformable = arFragment.transformationSystem.selectedNode
            if (selectedTransformable != null) {
                val anchorNode = selectedTransformable.parent as AnchorNode
                anchorNode.anchor?.detach()
            }
        }
//        settingsButton.setOnClickListener {
//            if (modelRadioGroup.isVisible) {
//                modelRadioGroup.visibility = INVISIBLE
//            } else {
//                modelRadioGroup.visibility = VISIBLE
//            }
//        }
    }

//    private fun modelByRadioButton(radioButtonId: Int): Int {
//        return when (radioButtonId) {
//            R.id.camera -> R.raw.camera
//            R.id.xbox_controller -> R.raw.xbox_controller
//            else -> throw Exception("No such model")
//        }
//    }

}