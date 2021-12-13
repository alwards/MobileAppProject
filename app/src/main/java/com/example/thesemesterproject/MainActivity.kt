package com.example.thesemesterproject

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.android.billingclient.api.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    lateinit var billingClient: BillingClient
    lateinit var iv_card1: ImageView
    lateinit var iv_card2: ImageView

    lateinit var tv_player1: TextView
    lateinit var tv_player2: TextView
    lateinit var b_deal: Button

    lateinit var random: Random

    var card1: Int = 0
    var card2: Int = 0

    var player1 = 0
    var player2 = 0

    var cardBackColor: String = "blue"
    var cardColor: String = "reg"

    var arrayOfCards = intArrayOf(
        R.drawable.cardclubs2,
        R.drawable.cardclubs3,
        R.drawable.cardclubs4,
        R.drawable.cardclubs5,
        R.drawable.cardclubs6,
        R.drawable.cardclubs7,
        R.drawable.cardclubs8,
        R.drawable.cardclubs9,
        R.drawable.cardclubs10,
        R.drawable.cardclubsj,
        R.drawable.cardclubsq,
        R.drawable.cardclubsk,
        R.drawable.cardclubsa
    )

    var arrayOfDifCards = intArrayOf(
        R.drawable.card2,
        R.drawable.card3,
        R.drawable.card4,
        R.drawable.card5,
        R.drawable.card6,
        R.drawable.card7,
        R.drawable.card8,
        R.drawable.card9,
        R.drawable.card10,
        R.drawable.card11,
        R.drawable.card12,
        R.drawable.card13,
        R.drawable.card1
    )


    fun changeCards() {
        var x: Int = 0
        if (cardColor != "reg") {
            while (x < arrayOfCards.size) {
                arrayOfCards[x] = arrayOfDifCards[x]
                x += 1
            }
        }
    }


    fun changeBacks(){
        iv_card1.setImageResource(R.drawable.cardbackgreen1)
        iv_card2.setImageResource(R.drawable.cardbackgreen1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        random = Random

        iv_card1 = findViewById(R.id.iv_card1)
        iv_card2 = findViewById(R.id.iv_card2)


        iv_card1.setImageResource(R.drawable.cardbackblue1)
        iv_card2.setImageResource(R.drawable.cardbackblue1)





        tv_player1 = findViewById(R.id.tv_player1)
        tv_player2 = findViewById(R.id.tv_player2)

        b_deal = findViewById(R.id.b_deal)
        b_deal.setOnClickListener {
            card1 = random.nextInt(arrayOfCards.size)
            card2 = random.nextInt(arrayOfCards.size)

            setCardImage(card1, iv_card1)
            setCardImage(card2, iv_card2)

            if (card1 > card2) {
                player1++
                tv_player1.text = "Player 1: $player1"
            } else {
                player2++
                tv_player2.text = "Player 2: $player2"
            }
        }

        fun testConsumePurchase(purchase: Purchase) {
            val params = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken).build()
            billingClient.consumeAsync(params) { _, _, ->
                print("consumed")
            }
        }

        val purchaseUpdateListener =
            PurchasesUpdatedListener { billingResult, purchases ->
                print("****************************************Listening***************************")
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (purchase in purchases) {
                        handlePurchase(purchase)
                        //testConsumePurchase(purchase)
                    }
                } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                    // Handle an error caused by a user cancelling the purchase flow.
                    print("****************************************Canceled?***************************")
                    /*if (purchases != null) {
                        for (purchase in purchases) {
                            //handle purchase
                            testConsumePurchase(purchase)
                        }
                    }*/
                } else {
                    // Handle any other error codes.
                    Log.d("fjdklsafjdslkfal", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                    Log.d("purchase", purchases?.size.toString())
                }
            }

        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases().setListener(purchaseUpdateListener)
            .enablePendingPurchases().build()

        connectToGooglePlayBilling()
    }




    fun connectToGooglePlayBilling(){
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    getProductDetails()
                    billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP){billingResult, purchases->
                        Log.d("fjkdfksks", "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%")
                        if(billingResult.responseCode == BillingClient.BillingResponseCode.OK){
                            Log.d("bruh", "*****************************************************")
                            if(purchases != null){
                                Log.d("Finally", "So it isn't null")
                                Log.d("size: ", purchases.size.toString())
                                for(item in purchases){
                                    if(item.skus.contains("green_back")){
                                        Log.d("contains green","green Contained")
                                        changeBacks()
                                    }
                                    if(item.skus.contains("differe_card")){
                                        Log.d("contains diff","diff Contained")
                                        cardColor = "diff"
                                        changeCards()
                                    }
                                }

                            } else {
                                Log.d("okay", "I see")
                            }

                        }else{
                            Log.d("Non-ok: ", billingResult.responseCode.toString())
                            }
                    }
                    Log.d("after", "2222222222222222222222222222222222222222")


                }
            }
            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
               connectToGooglePlayBilling()
            }
        })
    }


     fun handlePurchase(purchase: Purchase) {
        // Purchase retrieved from BillingClient#queryPurchasesAsync or your PurchasesUpdatedListener.

         //print(purchase.skus)
        // Verify the purchase.
        // Ensure entitlement was not already granted for this purchaseToken.
        // Grant entitlement to the user.

        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken).build()
                billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    val billingResponseCode = billingResult.responseCode
                    val billingDebugMessage = billingResult.debugMessage
                }
            }
                /*if(purchase.skus[1] == "green_back" && purchase.purchaseState == Purchase.PurchaseState.PURCHASED){
                    print("green")
                    cardBackColor = "green"
                    changeBacks()

                }
                if(purchase.skus[0] == "differe_card" && purchase.purchaseState == Purchase.PurchaseState.PURCHASED){
                    print("change")
                    cardColor = "dif"
                    changeCards()
                }*/
        }


    }

    fun getProductDetails(){
        val productIds = arrayListOf<String>()
        productIds.add("green_back")
        productIds.add("differe_card")

        val skuList = ArrayList<String>()
        skuList.add("green_back")
        skuList.add("differe_card")
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)


        var activity: Activity = this
        billingClient?.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            if( billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null){
                var greenTextView: TextView = findViewById(R.id.greenText)
                var greenButton: Button = findViewById(R.id.greenButton)
                var greenItemInfo: SkuDetails = skuDetailsList[1]
                var changeTheTextView: TextView = findViewById(R.id.changeText)
                var changeTheButton: Button = findViewById(R.id.changeButton)
                var changeItemInfo: SkuDetails = skuDetailsList[0]
                greenTextView.text = greenItemInfo.title
                greenButton.text = greenItemInfo.price
                changeTheTextView.text = changeItemInfo.title
                changeTheButton.text = changeItemInfo.price

                greenButton.setOnClickListener {
                    billingClient.launchBillingFlow(
                        activity,
                        BillingFlowParams.newBuilder().setSkuDetails(greenItemInfo).build()
                    )
                }
                changeTheButton.setOnClickListener {
                    billingClient.launchBillingFlow(
                        activity,
                        BillingFlowParams.newBuilder().setSkuDetails(changeItemInfo).build()
                    )
                }
            }
        }

             //leverage querySkuDetails Kotlin extension function
            //val skuDetailsResult = withContext(Dispatchers.IO) {
               //billingClient.querySkuDetails(params.build())
            //}

            // Process the result.
            //if( skuDetailsResult == BillingClient.BillingResponseCode.OK
              //  && skuDetailsResult != null){
              //  var greenTextView: TextView = findViewById(R.id.greenText)
              //  var greenButton: Button = findViewById(R.id.greenButton)
               // var greenItemInfo: SkuDetails = skuDetailsResult.get(0)
               // greenTextView.setText(greenItemInfo.title)
              //  greenButton.setText(greenItemInfo.price)
           // }

    }
    private fun setCardImage(number: Int, image: ImageView){
        if(cardColor == "reg") {
            image.setImageResource(arrayOfCards[number])
        }else{
            image.setImageResource(arrayOfDifCards[number])
        }

    }

}