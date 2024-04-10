import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog

class ShowDialog() {
    fun create(
        context: Context,
        title: String,
        message: String,
        positiveButton: String? = null,
        negativeButton: String? = null,
        onPositiveClick: (() -> Unit)? = null,
        onNegativeClick: (() -> Unit)? = null
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)

        positiveButton?.let {
            builder.setPositiveButton(it) { dialog, _ ->
                Log.d("DialogView", "positive button clicked")
                dialog.dismiss()
                onPositiveClick?.invoke()
            }
        }

        negativeButton?.let {
            builder.setNegativeButton(it) { dialog, _ ->
                Log.d("DialogView", "negative button clicked")
                dialog.dismiss()
                onNegativeClick?.invoke()
            }
        }

        val dialog = builder.create()
        dialog.show()
    }
}
