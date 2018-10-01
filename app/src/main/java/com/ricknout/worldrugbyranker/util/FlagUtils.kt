package com.ricknout.worldrugbyranker.util

import android.util.Log

object FlagUtils {

    fun getFlagEmojiForTeamAbbreviation(teamAbbreviation: String): String = when (teamAbbreviation) {
        "NZL" -> "\uD83C\uDDF3\uD83C\uDDFF" // New Zealand
        "IRE" -> "\uD83C\uDDEE\uD83C\uDDEA" // Ireland
        "WAL" -> "" // Wales
        "ENG" -> "" // England
        "RSA" -> "" // South Africa
        "SCO" -> "" // Scotland
        "AUS" -> "" // Australia
        "FRA" -> "" // France
        "ARG" -> "" // Argentina
        "FJI" -> "" // Fiji
        "JPN" -> "" // Japan
        "TGA" -> "" // Tonga
        "GEO" -> "" // Georgia
        "ITA" -> "" // Italy
        "USA" -> "" // USA
        "SAM" -> "" // Samoa
        "ROM" -> "" // Romania
        "URU" -> "" // Uruguay
        "RUS" -> "" // Russia
        "ESP" -> "" // Spain
        "HKG" -> "" // Hong Kong
        "NAM" -> "" // Namibia
        "CAN" -> "" // Canada
        "POR" -> "" // Portugal
        "BEL" -> "" // Belgium
        "BRA" -> "" // Brazil
        "NED" -> "" // Netherlands
        "KEN" -> "" // Kenya
        "GER" -> "" // Germany
        "CHL" -> "" // Chile
        "KOR" -> "" // Korea
        "CZE" -> "" // Czechia
        "SUI" -> "" // Switzerland
        "LTU" -> "" // Lithuania
        "POL" -> "" // Poland
        "COL" -> "" // Columbia
        "UKR" -> "" // Ukraine
        "PAR" -> "" // Paraguay
        "ZIM" -> "" // Zimbabwe
        "MLT" -> "" // Malta
        "TUN" -> "" // Tunisia
        "UGA" -> "" // Uganda
        "SRI" -> "" // Sri Lanka
        "MDA" -> "" // Moldova
        "CIV" -> "" // Cote D'Ivoire
        "MAR" -> "" // Morocco
        "MAS" -> "" // Malaysia
        "MEX" -> "" // Mexico
        "TTO" -> "" // Trinidad & Tobago
        "MAD" -> "" // Madagascar
        "CAY" -> "" // Cayman Islands
        "PHP" -> "" // Philippines
        "COK" -> "" // Cook Islands
        "SEN" -> "" // Senegal
        "CRO" -> "" // Croatia
        "SWE" -> "" // Sweden
        "GUY" -> "" // Guyana
        "ISR" -> "" // Israel
        "LAT" -> "" // Latvia
        "SIN" -> "" // Singapore
        "KAZ" -> "" // Kazakhstan
        "VEN" -> "" // Venezuela
        "TPE" -> "" // Chinese Taipei
        "LUX" -> "" // Luxembourg
        "JAM" -> "" // Jamaica
        "ZAM" -> "" // Zambia
        "PNG" -> "" // Papua New Guinea
        "HUN" -> "" // Hungary
        "GUM" -> "" // Guam
        "AND" -> "" // Andorra
        "BIH" -> "" // Bosnia & Herzegovina
        "UAE" -> "" // United Arab Emirates
        "NGA" -> "" // Nigeria
        "BWA" -> "" // Botswana
        "AUT" -> "" // Austria
        "BER" -> "" // Bermuda
        "SVN" -> "" // Slovenia
        "THA" -> "" // Thailand
        "DEN" -> "" // Denmark
        "PER" -> "" // Peru
        "SVG" -> "" // St Vincent and Grenadines
        "SRB" -> "" // Serbia
        "BRB" -> "" // Barbados
        "IND" -> "" // India
        "PYF" -> "" // Tahiti
        "BHS" -> "" // Bahamas
        "CHN" -> "" // China
        "SWZ" -> "" // Swaziland
        "GHA" -> "" // Ghana
        "UZB" -> "" // Uzbekistan
        "PAK" -> "" // Pakistan
        "MUS" -> "" // Mauritius
        "CTR" -> "" // Costa Rica
        "BUL" -> "" // Bulgaria
        "RWA" -> "" // Rwanda
        "NOR" -> "" // Norway
        "IDO" -> "" // Indonesia
        "NIU" -> "" // Niue Island
        "FIN" -> "" // Finland
        "CAM" -> "" // Cameroon
        "SOL" -> "" // Solomon Islands
        "MON" -> "" // Monaco
        "HEL" -> "" // Greece
        "VAN" -> "" // Vanuatu
        "ASM" -> "" // American Samoa
        else -> {
            Log.e(TAG, "Could not find flag emoji for $teamAbbreviation")
            "\uD83C\uDFF3️" // White flag if team can't be found
        }
    }

    private const val TAG = "FlagUtils"
}