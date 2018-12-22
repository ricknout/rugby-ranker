# Rugby Ranker 🏉

[![CircleCI](https://circleci.com/gh/nicholasrout/rugby-ranker/tree/master.svg?style=shield)](https://circleci.com/gh/nicholasrout/rugby-ranker/tree/master)
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)

Rugby Ranker is a **work-in-progess** Android app for viewing and predicting the latest World Rugby rankings.

It displays the latest international rankings, fixtures and results and makes use of the [World Rugby 'Points Exchange' system](https://www.world.rugby/rankings/explanation) in order to predict changes in team positions and points.

<br>

<p align="center">
  <img alt="Rugby Ranker Demo" src="/art/demo.gif" width="320" />
</p>

<p align="center">
  <a href="https://play.google.com/store/apps/details?id=com.ricknout.rugbyranker" target="_blank">
    <img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' width="320" />
  </a>
</p>

## Android development

Rugby Ranker attempts to make use of the latest Android libraries and best practices:
* Entirely written in [Kotlin](https://kotlinlang.org/) with [ktlint](https://github.com/shyiko/ktlint) for code style
* Makes use of [Android Jetpack](https://developer.android.com/jetpack/), including:
  * All appropriate [Architecture Components](https://developer.android.com/topic/libraries/architecture/), including **Lifecycles**, **LiveData**, **ViewModel**, **Room**, **Paging**, **Navigation** and **WorkManager**
  * [ConstraintLayout](https://developer.android.com/reference/android/support/constraint/ConstraintLayout) 2.0 for layouts
  * [EmojiCompat](https://developer.android.com/guide/topics/ui/look-and-feel/emoji-compat) for emoji compatibility on older platforms
  * [Android KTX](https://developer.android.com/kotlin/ktx) for more fluent use of Android APIs
* [Retrofit](https://square.github.io/retrofit/) for networking
* [Dagger](https://google.github.io/dagger/) for dependency injection
* Designed and built using Material Design [tools](https://material.io/tools/) and [components](https://material.io/develop/android/)

## Inspiration

Rugby Ranker was inspired by [@rawling](https://github.com/rawling)'s [wr-calc](https://rawling.github.io/wr-calc/) web app, aiming to be a native Android version with a focus on delightful UX/UI design.

## Contributions

Please feel free to file an issue for errors, suggestions or feature requests. Pull requests are also encouraged.

## License

```
Copyright 2018 Nick Rout

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements. See the NOTICE file distributed with this work for
additional information regarding copyright ownership. The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
```
