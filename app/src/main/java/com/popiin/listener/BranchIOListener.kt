package com.popiin.listener

import io.branch.referral.BranchError

open class BranchIOListener {
   open fun onLinkCreate(url: String?) {}
   open fun onLinkCreateError(error: BranchError?) {}
}