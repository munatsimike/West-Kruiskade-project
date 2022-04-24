package nl.project.westkruiskade.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.core.view.isVisible
import nl.project.westkruiskade.util.alertDialogueBox
import nl.project.westkruiskade.util.redirectToPreviousFragment
import nl.project.westkruiskade.util.snackbarLong
import nl.project_.west_kruiskade.R
import nl.project_.west_kruiskade.databinding.ContactFormLayoutBinding
import kotlin.properties.Delegates

class ContactFormFragment :
    BaseFragment<ContactFormLayoutBinding>(ContactFormLayoutBinding::inflate),
    View.OnFocusChangeListener {

    private lateinit var invalidEmailAddressError: String
    private lateinit var emptyMessageError: String
    private var alertCount by Delegates.notNull<Int>()
    private lateinit var emailAddress: String
    private lateinit var message: String
    private var subject: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        invalidEmailAddressError = getString(R.string.email_address_error_msg)
        emptyMessageError = getString(R.string.message_body_error_msg)
        emailAddress = invalidEmailAddressError
        alertCount = 0;
        message = emptyMessageError
        binding.submitFormBtn.setOnClickListener(this)
        binding.backBtn.backBtn.setOnClickListener(this)
        setFocusChangeLister()
    }

    override fun onResume() {
        super.onResume()
        clearForm()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            binding.submitFormBtn.id -> {
                setFieldFocus(binding.editTextEmailAddress)
                sendEmail()
            }

            binding.backBtn.backBtn.id -> {
                setFieldFocus(binding.submitFormBtn)
                redirectToPreviousFragment(this)
            }
        }
    }

    // handle focus change events
    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (!hasFocus) {
            when (view?.id) {
                R.id.edit_text_email_address -> {
                    if (binding.editTextEmailAddress.text.isValidEmail()) {
                        emailAddress = binding.editTextEmailAddress.text.toString()
                        setValidFieldBackground(binding.editTextEmailAddress)
                        binding.emailCheckCircle.isVisible = true
                    } else {
                        emailAddress = invalidEmailAddressError
                        requireView().snackbarLong(emailAddress).show()
                        setErrorBackground(binding.editTextEmailAddress)
                        if (binding.emailCheckCircle.isVisible)
                            binding.emailCheckCircle.isVisible = false
                    }
                }
                R.id.editTextSubject -> {
                    if (binding.editTextSubject.text.trim().isNotEmpty()) {
                        subject = binding.editTextSubject.text.toString()
                    }
                }

                R.id.edit_text_message_body -> {
                    val msg = binding.editTextMessageBody
                    if (msg.text.length < 8) {
                        message = emptyMessageError
                        requireView().snackbarLong(message).show()
                        setErrorBackground(msg)
                        return
                    }

                    if (msg.text.trim().isEmpty()) {
                        requireView().snackbarLong(message).show()
                        setErrorBackground(msg)
                        return
                    }

                    message = msg.text.toString()
                    setValidFieldBackground(msg)
                }
            }
        }
    }

    private fun clearForm() {
        binding.editTextEmailAddress.clearFocus()
        binding.editTextMessageBody.setText(R.string.blank_txt)
        binding.editTextSubject.setText(R.string.blank_txt)
        binding.editTextEmailAddress.setText(R.string.blank_txt)
        binding.emailCheckCircle.isVisible = false
        binding.editTextMessageBody.setBackgroundResource(R.drawable.white_rectangle_no_stroke)
        binding.editTextEmailAddress.setBackgroundResource(R.drawable.white_rectangle_no_stroke)
    }

    private fun setFieldFocus(view: View) {
        view.requestFocus()
    }

    private fun setValidFieldBackground(view: View) {
        view.setBackgroundResource(R.drawable.transparent_rectangle)
    }

    private fun setErrorBackground(view: View) {
        view.setBackgroundResource(R.drawable.transparent_rectangle_red_stroke)
    }

    private fun setFocusChangeLister() {
        binding.editTextEmailAddress.onFocusChangeListener = this
        binding.editTextSubject.onFocusChangeListener = this
        binding.editTextMessageBody.onFocusChangeListener = this
    }

    private fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(
        this
    ).matches()

    private fun sendEmail() {
        if (emailAddress == invalidEmailAddressError) {
            requireView().snackbarLong(invalidEmailAddressError).show()
            setFieldFocus(binding.editTextEmailAddress)
            return
        }

        if (message == emptyMessageError) {
            requireView().snackbarLong(emptyMessageError).show()
            setFieldFocus(binding.editTextMessageBody)
            return
        }

        if (subject.isNullOrEmpty() && alertCount == 0) {
            requireContext().alertDialogueBox(
                getString(R.string.subject),
                getString(R.string.subject_filed_empty)
            )
            alertCount++
            return
        }

        val intent = Intent(Intent.ACTION_SENDTO)
        intent.apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }

        startActivity(Intent.createChooser(intent, "Email via..."))
    }
}


