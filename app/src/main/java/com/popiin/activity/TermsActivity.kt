package com.popiin.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import com.popiin.BaseActivity
import com.popiin.R
import com.popiin.databinding.ActivityTermsBinding

class TermsActivity : BaseActivity<ActivityTermsBinding>() {
    override fun getLayout(): Int {
        return R.layout.activity_terms
    }

    override fun initViews() {
        binding.tvTerms.tvHeader.text = resources.getString(R.string.lbl_general)
        binding.tvTerms1.tvDescription.text = resources.getString(R.string.terms1)

        binding.tvDataPolicy.tvHeader.text = resources.getString(R.string.lbl_data_policy)
        val fullText = resources.getString(R.string.dataPolicy1)
        val clickableText = "data and privacy policy"

        val spannableString = SpannableString(fullText)
        val startIndex = fullText.indexOf(clickableText)
        val endIndex = startIndex + clickableText.length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(mActivity, PrivacyActivity::class.java))
            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.BLACK // Set the text color to dark
                ds.isUnderlineText = true // Optional: Keep underline for visibility
            }
        }

        // Apply ClickableSpan
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Apply Bold Style
        spannableString.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


        binding.tvDataPolicy1.tvDescription.text = spannableString
        binding.tvDataPolicy1.tvDescription.movementMethod = LinkMovementMethod.getInstance()

        //    binding.tvDataPolicy1.tvDescription.text = resources.getString(R.string.dataPolicy1)

        binding.tvService.tvHeader.text = resources.getString(R.string.lbl_service)
        binding.tvService1.tvDescription.text = resources.getString(R.string.service1)


        binding.tvCondition.tvHeader.text = resources.getString(R.string.lbl_condition)
     //   binding.tvCondition1.tvDescription.text = resources.getString(R.string.condition1)
        binding.tvCondition1.tvDescription.text =
                "As a condition of use, you agree not to violate or use the service for any purpose that is prohibited by these terms and conditions.\n" +
                "You are responsible for your use of the website or app, and services, and for any personal information and content that you provide, including compliance with applicable laws, rules, and regulations. You may only use our services for your own personal and business purposes, but you must not use our website or app for any other purposes.\n" +
                "We are aware that our services will give users direction and allow users to travel to locations, places and venues; therefore, we strongly advise that you carry out safety checks and you do not travel to places that may be unsafe, and to take care when making travel arrangements to ensure you obey traffic and safety laws. In connection with your use of our website or app, we may send you service updates, messages, and other information from time to time to keep you informed.\n" +
                "Using our website or app does not give you ownership of any intellectual property rights in our app, website or the content you access. You may not use content from our website or app unless you obtain permission from its owner or are otherwise permitted by law. These terms do not grant you the right to use any content used throughout our website or app for your own personal use. Do not remove, obscure, interfere or alter any content or information that does not belong to you or have not been granted access to use.\n" +
                "If you use content covered by intellectual property rights that we have and make available (for example, images, designs, videos etc. that we provide, which you add to the content that you create or share), we retain all rights to that content (but not yours).\n" +
                "Without prejudice to our other rights under these terms and conditions, if you breach any provision of these terms and conditions in any way, or if we reasonably suspect that you have breached these terms and conditions in any way, popiin ™ reserves the right to suspend or stop providing our website or app to you, delete and remove any of your personal information, data and content from the website or app at any time.\n" +
                "Your personal information, and the use of your content by us in accordance with these terms and conditions must not be libellous or maliciously false, unlawful, threatening, abusive, harassing, defamatory, deceptive, fraudulent, invasive of another\'s privacy, tortious, obscene, vulgar, pornographic, offensive, profane, contain or depict nudity, contain or depict sexual activity, promote bigotry, discrimination or violence, or be otherwise inappropriate as determined by popiin ™.\n" +
                "You must not infringe any copyright, moral, database, trade secret, trademark, design right, right in passing off, or other intellectual property right; infringe any right of confidence, right of privacy or right under data protection legislation; be untrue, false, inaccurate or misleading; interfere with, disrupt or damage the service given to any user in any manner, including submitting a virus to the website or app or attempting to overload, flood, spam, mail bomb or crash the website or app; scan or monitor the website or app for data gathering; scan or test the security or configuration of the website or app; or access data not intended for you, such as logging into an account you are not authorized to access.\n" +
                "You must not breach racial or religious hatred or discrimination legislation; depict violence in an explicit, graphic or gratuitous manner; be pornographic, lewd, suggestive or sexually explicit; contain any instructions or advice that could cause illness, injury or death, or any other loss or damage; be offensive, deceptive, fraudulent, threatening, abusive, harassing, antisocial, menacing, hateful, discriminatory or inflammatory; or cause annoyance, inconvenience or needless anxiety to any person.\n" +
                "Furthermore, you must not use our website or app in any unlawful, illegal, fraudulent or harmful way; or in connection with any such activity. You must not conduct any systematic or automated data collection (such as scraping, data mining, data extraction, or data harvesting) on or in relation to our website or app without our written consent.\n" +
                "Do not misuse our website, app or services. Do not try to access our content using any technique other than those we provide. Use our services only as permitted by law. Unless you own the rights to your content, you must not republish, sell, rent or license content from our website or app, or exploit it commercially. You must not attempt to bypass any access restrictions. We may restrict access to parts or all of our services at our discretion.\n" +
                "We support and promote venues in delivering exceptional experiences, ensuring users can discover and book venues, events and activities that match their interests. When attending an event, all guests must follow the venue\'s policies and rules. Please ensure the venue is safe before visiting and treat all staff with respect.\n" +
                "Guests must follow the venue\'s policies on drugs and alcohol and drink responsibly.\n" +
                "Age verification may be required using valid photo ID to comply with laws and event policies. Inform the venue of any allergies before ordering food or drinks.\n" +
                "We are not responsible for emergencies, harm or death due to allergen reactions, substance abuse, visiting unsafe areas, or ignoring rules.\n" +
                "All events, bookings and offers are subject to availability and may be limited or withdrawn. Guests must follow all venue or event terms. For help, speak with the venue manager or event staff.\n" +
                "Children under 16 should be accompanied by an adult. Venues may refuse entry based on dress code, age, or other criteria.\n" +
                "Venue staff and organizers have full authority to enforce policies. Their decisions are final.\n" +
                "Thank you for helping to keep experiences safe and enjoyable for everyone."

        binding.tvCookie.tvHeader.text = resources.getString(R.string.lbl_cookie)
        binding.tvCookie1.tvDescription.text = resources.getString(R.string.cookie1)

        binding.tvRegistration.tvHeader.text = resources.getString(R.string.lbl_registration)
        binding.tvRegistration1.tvDescription.text =
            resources.getString(R.string.registration1)

        binding.tvCancellation.tvHeader.text = resources.getString(R.string.lbl_cancellation)
        binding.tvCancellation1.tvDescription.text =
            resources.getString(R.string.cancellation1)

        binding.tvWarranties.tvHeader.text = resources.getString(R.string.lbl_warranties)
        binding.tvWarranties1.tvDescription.text = resources.getString(R.string.warranties1)

        binding.tvPayment.tvHeader.text = resources.getString(R.string.lbl_payment)
        binding.tvPayment1.tvDescription.text = resources.getString(R.string.payment1)

        binding.tvLimitations.tvHeader.text = resources.getString(R.string.lbl_limitation)
        binding.tvLimitations1.tvDescription.text = resources.getString(R.string.limitation1)

        binding.tvThirdParty.tvHeader.text = resources.getString(R.string.lbl_third_party)
        binding.tvThirdParty1.tvDescription.text = resources.getString(R.string.third_party1)

        binding.tvSeverability.tvHeader.text = resources.getString(R.string.lbl_severability)
        binding.tvSeverability1.tvDescription.text = resources.getString(R.string.severability1)

        binding.tvVariation.tvHeader.text = resources.getString(R.string.lbl_variation)
        binding.tvVariation1.tvDescription.text = resources.getString(R.string.variation1)

        binding.tvJurisdiction.tvHeader.text = resources.getString(R.string.lbl_jurisdiction)
        binding.tvJurisdiction1.tvDescription.text = resources.getString(R.string.jurisdiction1)

        binding.tvDisclosures.tvHeader.text = resources.getString(R.string.lbl_disclosures)
        binding.tvDisclosures1.tvDescription.text = resources.getString(R.string.disclosures1)

        binding.tvWithdraw.tvHeader.text= resources.getString(R.string.lbl_withdrawals)
        binding.tvWithdrawDes.tvDescription.text= getString(R.string.lbl_withdraw_des)

        binding.tvAssignment.tvHeader.text= getString(R.string.lbl_assignment_new)
        binding.tvAssignmentDes.tvDescription.text= getString(R.string.lbl_assignment_new1)

        binding.topHeader.ivBack.setOnClickListener {
            finish()
        }
    }

}