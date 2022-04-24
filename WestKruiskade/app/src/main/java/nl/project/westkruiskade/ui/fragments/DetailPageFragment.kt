package nl.project.westkruiskade.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import nl.project.westkruiskade.model.AudioPlayer
import nl.project.westkruiskade.model.AudioUrl
import nl.project.westkruiskade.util.layoutTransition
import nl.project.westkruiskade.util.redirectToPreviousFragment
import nl.project_.west_kruiskade.databinding.DetailPageLayoutBinding
import javax.inject.Inject

@AndroidEntryPoint
class DetailPageFragment : BaseFragment<DetailPageLayoutBinding>(DetailPageLayoutBinding::inflate) {
    private val args: DetailPageFragmentArgs by navArgs()

    @Inject
    lateinit var myPlayer: AudioPlayer

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateDetailPage()
        setOnclickListener()
        onScrollListener()
    }


    override fun onPause() {
        super.onPause()
        if (myPlayer.isPlayerShowing()) {
            myPlayer.closeAudioPlayer()
        }
    }

    private fun populateDetailPage() {
        with(binding) {
            with(args.exhibit) {
                if (images.isNotEmpty()){
                    ivExhibit.load(images[0].imageUri)
                }
                tvArtistName.text = artistName
                tvExhibitAddress.text = location
                tvAboutArtist.text = story.replace("|", "\n")
            }
        }
    }

    // open exhibit address on google maps(turn by turn navigation)
    private fun openGoogleMaps() {
        val exhibitAddress = args.exhibit.location.replace(" ", "+")
        val gmmIntentUri = Uri.parse("google.navigation:q=${exhibitAddress}&mode=w")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    private fun setOnclickListener() {
        binding.tvRoute.setOnClickListener(this)
        binding.backBtn.backBtn.setOnClickListener(this)
        binding.locationIcon.setOnClickListener(this)
        binding.playAudioIcon.setOnClickListener(this)
        binding.expandToggleBtn.setOnClickListener(this)
    }

    private fun onScrollListener() {
        binding.detailScrollView.setOnScrollChangeListener { _, _, _, _, _ ->
            if (getGuideLineHeight() == 0.5f) {
                binding.cardGuideline.setGuidelinePercent(0.12f)
                scrollViewTransition()
            }

        }
    }

    private fun getGuideLineHeight(): Float {
        return (binding.cardGuideline.layoutParams as ConstraintLayout.LayoutParams).guidePercent
    }

    private fun scrollViewTransition() {
        binding.detailPageFragment.layoutTransition(500)
    }

    // handle click events
    override fun onClick(view: View?) {

        when (view?.id) {
            binding.tvRoute.id, binding.locationIcon.id -> {
                openGoogleMaps()
            }

            binding.backBtn.backBtn.id -> {
                redirectToPreviousFragment(this)
            }

            binding.playAudioIcon.id -> {
                if (!myPlayer.isPlayerShowing()) {
                    if (args.exhibit.audios.isNotEmpty()){
                        myPlayer.intAudioUrl(AudioUrl(args.exhibit.audios[0].audioUri))
                    }
                    myPlayer.showPlayerAtLocation(binding.detailPageFragment)

                } else if (myPlayer.isPlayerShowing()) {
                    myPlayer.closeAudioPlayer()
                } else {
                    myPlayer.showPlayerAtLocation(binding.detailPageFragment)
                }
            }
            binding.expandToggleBtn.id -> {
                if (getGuideLineHeight() == 0.12f) {
                    binding.cardGuideline.setGuidelinePercent(0.5f)
                } else {
                    binding.cardGuideline.setGuidelinePercent(0.12f)
                }
                scrollViewTransition()
            }
        }
    }
}


