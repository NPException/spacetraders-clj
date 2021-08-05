## Gettings Started
In order to get familiar with this library, follow the
[SpaceTraders "Getting started" guide](https://spacetraders.io/docs/guide)
as close as possible. So let's fire up a REPL and require the library's core namespace:

```clojure
(require '[de.npexception.spacetraders-clj.core :as api])
```

The API is only accessible if you have an access token. You can claim a username and
generate a token by hitting the following endpoint. Make sure you save this token
and don't share it with anyone. You can only generate a single token once per username.

```clojure
(api/claim-username! "Arthur Dent")
```
```clojure
=> {:token "b3b14cf6-73d6-4a72-9c72-14a3d29fb318",
    :user {:username "Arthur Dent",
           :credits 0,
           :ships [],
           :loans []}}
```

We'll store the token in a var for easy access.

```clojure
(def token "b3b14cf6-73d6-4a72-9c72-14a3d29fb318")
```

### View Your User Account
Congratulations on taking your first steps toward the complete and total domination
of galactic trade! Let's take a quick look at your account.

```clojure
(api/my-account token)
```
```clojure
=> {:user {:username "Arthur Dent",
           :shipCount 0,
           :structureCount 0,
           :joinedAt "2021-08-05T14:45:12.179Z",
           :credits 0}}
```

Looks like you don't have much in the way of credits or assets. Let's see how we can fix that.

### View Available Loans
Let's kick off our trade empire by taking out a small low-risk loan.
We can use these funds to purchase our first ship and fill our cargo with something valuable.

```clojure
(api/available-loans token)
```
```clojure
=> {:loans [{:type "STARTUP",
             :amount 200000,
             :rate 40,
             :termInDays 2,
             :collateralRequired false}]}
```

### Take Out A Loan
Let's take out a small loan to kick off our new venture.

```clojure
(api/take-out-loan! token "STARTUP")
```
```clojure
=> {:credits 200000,
    :loan {:id "ckrz1tz5165575815s63v3ms6vk",
           :due "2021-08-07T15:03:19.331Z",
           :repaymentAmount 280000,
           :status "CURRENT",
           :type "STARTUP"}}
```

### View Ships To Purchase
Now our credits are looking much healthier! But remember, you will have to pay it back
by the due date. Let's buy a small ship to start shipping goods around and
hopefully make enough to pay back our loan.

```clojure
(api/system-ship-listings token "OE" "MK-I")
```
```clojure
=> {:shipListings [{:loadingSpeed 25,
                    :weapons 5,
                    :speed 1,
                    :type "JW-MK-I",
                    :class "MK-I",
                    :purchaseLocations [{:system "OE",
                                         :location "OE-PM-TR",
                                         :price 21125}],
                    :manufacturer "Jackshaw",
                    :maxCargo 50,
                    :plating 5}
                   {:loadingSpeed 100,
                    :weapons 5,
                    :speed 1,
                    :type "GR-MK-I",
                    :class "MK-I",
                    :purchaseLocations [{:system "OE",
                                         :location "OE-PM-TR",
                                         :price 42650}],
                    :manufacturer "Gravager",
                    :maxCargo 100,
                    :plating 10}
                   {:loadingSpeed 25,
                    :weapons 10,
                    :speed 2,
                    :type "EM-MK-I",
                    :class "MK-I",
                    :purchaseLocations [{:system "OE",
                                         :location "OE-PM-TR",
                                         :price 37750}],
                    :manufacturer "Electrum",
                    :maxCargo 50,
                    :plating 5}
                   ...]}
```

Choose one of the available ships and send a request to purchase it. The Jackshaw looks like a good cheap option.

### Purchase A Ship

```clojure
(api/buy-ship! token "OE-PM-TR" "JW-MK-I")
```
```clojure
=> {:credits 178875,
    :ship {:y 18,
           :cargo [],
           :loadingSpeed 25,
           :weapons 5,
           :speed 1,
           :type "JW-MK-I",
           :id "ckrz22g6j66771815s6onlxqkmn",
           :class "MK-I",
           :manufacturer "Jackshaw",
           :x 14,
           :spaceAvailable 50,
           :location "OE-PM-TR",
           :maxCargo 50,
           :plating 5}}
```

Save your ship ID to a variable so we can resuse it in a moment.

```clojure
(def ship-id "ckrz22g6j66771815s6onlxqkmn")
```

Now let's load it up with fuel and metals and see if we can make a profitable trade.

### Purchase Ship Fuel

```clojure
(api/place-purchase-order! token ship-id "FUEL" 20)
```
```clojure
=> {:credits 178815,
    :order {:good "FUEL", :quantity 20, :pricePerUnit 3, :total 60},
    :ship {:y 18,
           :cargo [{:good "FUEL", :quantity 20, :totalVolume 20}],
           :loadingSpeed 25,
           :weapons 5,
           :speed 1,
           :type "JW-MK-I",
           :id "ckrz22g6j66771815s6onlxqkmn",
           :class "MK-I",
           :manufacturer "Jackshaw",
           :x 14,
           :spaceAvailable 30,
           :location "OE-PM-TR",
           :maxCargo 50,
           :plating 5}}
```

### View Marketplace
Each location has a marketplace of goods. Let's see what's available to us.

```clojure
(api/location-marketplace token "OE-PM-TR")
```
```clojure
=> {:marketplace [{:symbol "FUSION_REACTORS",
                   :volumePerUnit 6,
                   :pricePerUnit 20398,
                   :spread 229,
                   :purchasePricePerUnit 20627,
                   :sellPricePerUnit 20169,
                   :quantityAvailable 15}
                  {:symbol "CONSTRUCTION_MATERIALS",
                   :volumePerUnit 1,
                   :pricePerUnit 140,
                   :spread 4,
                   :purchasePricePerUnit 144,
                   :sellPricePerUnit 136,
                   :quantityAvailable 11}
                  {:symbol "METALS",
                   :volumePerUnit 1,
                   :pricePerUnit 16,
                   :spread 1,
                   :purchasePricePerUnit 17,
                   :sellPricePerUnit 15,
                   :quantityAvailable 61951}
                  {:symbol "FUEL",
                   :volumePerUnit 1,
                   :pricePerUnit 2,
                   :spread 1,
                   :purchasePricePerUnit 3,
                   :sellPricePerUnit 1,
                   :quantityAvailable 91292}]}
```

Metals look like a solid trade good. Let's buy some.

```clojure
(api/place-purchase-order! token ship-id "METALS" 25)
```
```clojure
=> {:credits 178365,
    :order {:good "METALS", :quantity 25, :pricePerUnit 18, :total 450},
    :ship {:y 18,
           :cargo [{:good "FUEL", :quantity 20, :totalVolume 20}
                   {:good "METALS", :quantity 25, :totalVolume 25}],
           :loadingSpeed 25,
           :weapons 5,
           :speed 1,
           :type "JW-MK-I",
           :id "ckrz22g6j66771815s6onlxqkmn",
           :class "MK-I",
           :manufacturer "Jackshaw",
           :x 14,
           :spaceAvailable 5,
           :location "OE-PM-TR",
           :maxCargo 50,
           :plating 5}}
```

### Find Nearby Planet
Now we need to find a nearby planet to unload our cargo.

```clojure
(api/system-locations token "OE" "PLANET")
```
```clojure
=> {:locations [{:symbol "OE-PM",
                 :type "PLANET",
                 :name "Prime",
                 :x 13,
                 :y 16,
                 :allowsConstruction false,
                 :traits ["METAL_ORES" "SOME_ARABLE_LAND"]}
                {:symbol "OE-CR",
                 :type "PLANET",
                 :name "Carth",
                 :x 10,
                 :y 11,
                 :allowsConstruction false,
                 :traits ["METAL_ORES" "ARABLE_LAND" "RARE_METAL_ORES"]}
                {:symbol "OE-KO",
                 :type "PLANET",
                 :name "Koria",
                 :x -33,
                 :y -36,
                 :allowsConstruction false,
                 :traits ["SOME_METAL_ORES" "SOME_NATURAL_CHEMICALS"]}
                {:symbol "OE-UC",
                 :type "PLANET",
                 :name "Ucarro",
                 :x 74,
                 :y -15,
                 :allowsConstruction false,
                 :traits ["SOME_METAL_ORES" "NATURAL_CHEMICALS"]}]}
```

Looks like "Prime" is right next to us. Let's create a flight plan to send our ship to the planet.

### Create Flight Plan

```clojure
(api/create-flight-plan! token ship-id "OE-PM")
```
```clojure
=> {:flightPlan {:fuelRemaining 19,
                 :departure "OE-PM-TR",
                 :arrivesAt "2021-08-05T15:28:50.291Z",
                 :terminatedAt nil,
                 :timeRemainingInSeconds 35,
                 :createdAt "2021-08-05T15:28:14.292Z",
                 :id "ckrz2q0no70021715s6g3ewupqt",
                 :fuelConsumed 1,
                 :distance 2,
                 :shipId "ckrz22g6j66771815s6onlxqkmn",
                 :destination "OE-PM"}}
```

You can monitor your ship's flight plan until it arrives.
Save your flight plan ID to a variable so we can resuse it in a moment.

```clojure
(def flight-plan-id "ckrz2q0no70021715s6g3ewupqt")
```

### View Flight Plan

```clojure
(api/my-flight-plan-info token flight-plan-id)
```
```clojure
=> {:flightPlan {:fuelRemaining 19,
                 :departure "OE-PM-TR",
                 :arrivesAt "2021-08-05T15:28:50.291Z",
                 :terminatedAt "2021-08-05T15:28:45.747Z",
                 :timeRemainingInSeconds 0,
                 :createdAt "2021-08-05T15:28:14.292Z",
                 :id "ckrz2q0no70021715s6g3ewupqt",
                 :fuelConsumed 1,
                 :distance 2,
                 :shipId "ckrz22g6j66771815s6onlxqkmn",
                 :destination "OE-PM"}}
```

### Sell Trade Goods
Let's place a sell order for our metals.

```clojure
(api/place-sell-order! token ship-id "METALS" 25)
```
```clojure
=> {:credits 178965,
    :order {:good "METALS",
            :quantity 25,
            :pricePerUnit 24,
            :total 600},
    :ship {:cargo [{:good "FUEL",
                    :quantity 19,
                    :totalVolume 19}],
           :loadingSpeed 25,
           :weapons 5,
           :speed 1,
           :type "JW-MK-I",
           :id "ckrz22g6j66771815s6onlxqkmn",
           :class "MK-I",
           :manufacturer "Jackshaw",
           :x 13,
           :y 16,
           :spaceAvailable 31,
           :location "OE-PM",
           :maxCargo 50,
           :plating 5}}
```

### Next Steps
Congratulations! You made your first profitable trade.
You will likely want to trade in higher margin goods, but metals are a sure-fire way
to make some credits. Try buying another ship, exploring each market,
and maybe automate your way to wealth and glory!

When you're ready to pay back your loan, you can use the final function in this guide:

```clojure
(api/pay-off-loan! token "ckrz1tz5165575815s63v3ms6vk")
```
