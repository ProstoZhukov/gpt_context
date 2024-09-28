/**
 * Файл для хранения справочника кодов регионов и стран для подбора нужной маски.
 *
 * @author ps.smirnyh
 */
package ru.tensor.sbis.design.view.input.mask.phone.formatter.utils

/**
 * Список возможных кодов регионов России.
 */
internal val REGION = mapOf(
    "301" to arrayOf(
        "2",
        "30",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "37",
        "38",
        "39",
        "40",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "49",
        "50",
        "53"
    ),
    "302" to arrayOf(
        "2",
        "30",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "37",
        "38",
        "39",
        "40",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "49",
        "51",
        "52",
        "53",
        "55",
        "56",
        "57",
        "61",
        "62",
        "63",
        "64",
        "65",
        "66"
    ),
    "336" to arrayOf("22"),
    "341" to arrayOf(
        "2",
        "30",
        "32",
        "33",
        "34",
        "36",
        "38",
        "39",
        "41",
        "45",
        "47",
        "50",
        "51",
        "52",
        "53",
        "54",
        "55",
        "57",
        "58",
        "59",
        "61",
        "62",
        "63",
        "64",
        "65",
        "66"
    ),
    "342" to arrayOf(
        "40",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "48",
        "49",
        "50",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57",
        "58",
        "59",
        "60",
        "61",
        "62",
        "65",
        "66",
        "68",
        "69",
        "71",
        "72",
        "73",
        "74",
        "75",
        "76",
        "77",
        "78",
        "79",
        "91",
        "92",
        "93",
        "94",
        "96",
        "97",
        "98"
    ),
    "343" to arrayOf(
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "49",
        "5",
        "50",
        "55",
        "56",
        "57",
        "58",
        "60",
        "61",
        "62",
        "63",
        "64",
        "65",
        "67",
        "68",
        "69",
        "70",
        "71",
        "72",
        "73",
        "74",
        "75",
        "76",
        "77",
        "80",
        "83",
        "84",
        "85",
        "86",
        "87",
        "88",
        "89",
        "9",
        "91",
        "93",
        "94",
        "97",
        "98"
    ),
    "345" to arrayOf(
        "31",
        "33",
        "35",
        "37",
        "39",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "50",
        "51",
        "53",
        "54",
        "55",
        "56",
        "57",
        "6",
        "61"
    ),
    "346" to arrayOf(
        "1",
        "2",
        "3",
        "34",
        "37",
        "38",
        "43",
        "6",
        "63",
        "67",
        "68",
        "69",
        "7",
        "70",
        "71",
        "72",
        "74",
        "75",
        "76",
        "77",
        "78"
    ),
    "347" to arrayOf(
        "1",
        "12",
        "14",
        "16",
        "17",
        "3",
        "31",
        "39",
        "40",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "49",
        "50",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57",
        "58",
        "59",
        "60",
        "61",
        "62",
        "63",
        "64",
        "65",
        "66",
        "67",
        "68",
        "69",
        "70",
        "71",
        "72",
        "73",
        "74",
        "75",
        "76",
        "77",
        "78",
        "79",
        "8",
        "80",
        "81",
        "82",
        "84",
        "85",
        "86",
        "87",
        "88",
        "89",
        "9",
        "91",
        "92",
        "94",
        "95",
        "96",
        "97",
        "98"
    ),
    "349" to arrayOf(
        "22",
        "3",
        "32",
        "38",
        "4",
        "40",
        "6",
        "9",
        "92",
        "93",
        "94",
        "95",
        "96",
        "99"
    ),
    "351" to arrayOf(
        "3",
        "30",
        "31",
        "33",
        "34",
        "38",
        "39",
        "40",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "49",
        "50",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57",
        "58",
        "59",
        "60",
        "61",
        "63",
        "64",
        "65",
        "66",
        "67",
        "68",
        "69",
        "9",
        "91"
    ),
    "352" to arrayOf(
        "2",
        "30",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "37",
        "38",
        "39",
        "40",
        "41",
        "42",
        "43",
        "44",
        "45",
        "47",
        "48",
        "49",
        "5",
        "51",
        "52",
        "56",
        "57"
    ),
    "353" to arrayOf(
        "2",
        "30",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "37",
        "38",
        "39",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "49",
        "51",
        "52",
        "54",
        "55",
        "56",
        "57",
        "58",
        "59",
        "61",
        "62",
        "63",
        "64",
        "65",
        "66",
        "67",
        "68",
        "7",
        "79"
    ),
    "365" to arrayOf(
        "2",
        "4",
        "50",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57",
        "58",
        "59",
        "60",
        "61",
        "62",
        "63",
        "64",
        "65",
        "66",
        "67",
        "69"
    ),
    "381" to arrayOf(
        "2",
        "41",
        "50",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57",
        "58",
        "59",
        "60",
        "61",
        "62",
        "63",
        "64",
        "65",
        "66",
        "67",
        "68",
        "69",
        "70",
        "71",
        "72",
        "73",
        "74",
        "75",
        "76",
        "77",
        "78",
        "79"
    ),
    "382" to arrayOf(
        "2",
        "3",
        "41",
        "43",
        "44",
        "45",
        "46",
        "47",
        "49",
        "50",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57",
        "58",
        "59"
    ),
    "383" to arrayOf(
        "40",
        "41",
        "43",
        "45",
        "46",
        "47",
        "48",
        "49",
        "50",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57",
        "58",
        "59",
        "60",
        "61",
        "62",
        "63",
        "64",
        "65",
        "66",
        "67",
        "68",
        "69",
        "71",
        "72",
        "73"
    ),
    "384" to arrayOf(
        "2",
        "3",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "49",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57",
        "59",
        "63",
        "64",
        "66",
        "71",
        "72",
        "73",
        "74",
        "75"
    ),
    "385" to arrayOf(
        "11",
        "14",
        "2",
        "30",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "37",
        "38",
        "39",
        "4",
        "50",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57",
        "58",
        "59",
        "60",
        "61",
        "62",
        "63",
        "64",
        "65",
        "66",
        "67",
        "68",
        "69",
        "70",
        "71",
        "72",
        "73",
        "74",
        "75",
        "76",
        "77",
        "78",
        "79",
        "80",
        "81",
        "82",
        "83",
        "84",
        "85",
        "86",
        "87",
        "88",
        "89",
        "90",
        "91",
        "92",
        "93",
        "94",
        "95",
        "96",
        "97",
        "98",
        "99"
    ),
    "388" to arrayOf("2", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49"),
    "390" to arrayOf("2", "3", "32", "33", "34", "35", "36", "4", "41", "42", "44", "45", "47"),
    "391" to arrayOf(
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "37",
        "38",
        "39",
        "4",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "49",
        "50",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "58",
        "59",
        "60",
        "61",
        "62",
        "63",
        "64",
        "65",
        "66",
        "67",
        "68",
        "69",
        "70",
        "71",
        "72",
        "73",
        "74",
        "75",
        "76",
        "77",
        "78",
        "79",
        "9",
        "90",
        "91",
        "95",
        "96",
        "97",
        "99"
    ),
    "394" to arrayOf(
        "32",
        "33",
        "34",
        "35",
        "36",
        "37",
        "38",
        "39",
        "41",
        "42",
        "43",
        "44",
        "45",
        "50",
        "51",
        "52",
        "53",
        "75"
    ),
    "395" to arrayOf(
        "1",
        "10",
        "14",
        "2",
        "3",
        "30",
        "35",
        "36",
        "37",
        "38",
        "39",
        "40",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "48",
        "49",
        "5",
        "50",
        "51",
        "52",
        "53",
        "54",
        "57",
        "58",
        "60",
        "61",
        "62",
        "63",
        "64",
        "65",
        "66",
        "67",
        "68",
        "69",
        "73"
    ),
    "401" to arrayOf(
        "2",
        "41",
        "42",
        "43",
        "44",
        "45",
        "50",
        "51",
        "52",
        "53",
        "55",
        "56",
        "57",
        "58",
        "59",
        "61",
        "62",
        "63",
        "64",
        "77"
    ),
    "411" to arrayOf(
        "2",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "37",
        "38",
        "40",
        "41",
        "42",
        "43",
        "44",
        "45",
        "47",
        "50",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57",
        "58",
        "59",
        "60",
        "61",
        "62",
        "63",
        "64",
        "65",
        "66",
        "67",
        "68",
        "69"
    ),
    "413" to arrayOf("2", "41", "42", "43", "44", "45", "46", "47", "48", "53"),
    "415" to arrayOf(
        "2",
        "3",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "4",
        "42",
        "44",
        "45",
        "46",
        "47"
    ),
    "416" to arrayOf(
        "2",
        "31",
        "32",
        "33",
        "34",
        "36",
        "37",
        "38",
        "39",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "49",
        "5",
        "51",
        "52",
        "54",
        "58"
    ),
    "421" to arrayOf(
        "2",
        "35",
        "37",
        "38",
        "41",
        "42",
        "43",
        "44",
        "46",
        "47",
        "49",
        "51",
        "53",
        "54",
        "55",
        "56",
        "7"
    ),
    "423" to arrayOf(
        "31",
        "34",
        "35",
        "37",
        "39",
        "4",
        "44",
        "45",
        "46",
        "47",
        "49",
        "51",
        "52",
        "54",
        "55",
        "56",
        "57",
        "59",
        "6",
        "61",
        "62",
        "63",
        "65",
        "71",
        "72",
        "73",
        "74",
        "75",
        "76",
        "77"
    ),
    "424" to arrayOf(
        "2",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "37",
        "41",
        "42",
        "43",
        "44",
        "46",
        "47",
        "52",
        "53",
        "54",
        "55"
    ),
    "426" to arrayOf("22", "32", "63", "65", "66"),
    "427" to arrayOf("22", "3", "32", "33", "35", "36", "37", "38"),
    "471" to arrayOf(
        "2",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "37",
        "40",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "49",
        "50",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57",
        "58",
        "59",
        "92"
    ),
    "472" to arrayOf(
        "2",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "37",
        "38",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "5",
        "61",
        "62",
        "63"
    ),
    "473" to arrayOf(
        "40",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "50",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57",
        "61",
        "62",
        "63",
        "64",
        "65",
        "66",
        "67",
        "70",
        "71",
        "72",
        "74",
        "75",
        "76",
        "91",
        "94",
        "95",
        "96"
    ),
    "474" to arrayOf(
        "2",
        "61",
        "62",
        "63",
        "64",
        "65",
        "66",
        "67",
        "68",
        "69",
        "70",
        "71",
        "72",
        "73",
        "74",
        "75",
        "76",
        "77",
        "78",
        "79"
    ),
    "475" to arrayOf(
        "2",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "37",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "48",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57",
        "58",
        "59"
    ),
    "481" to arrayOf(
        "2",
        "30",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "37",
        "38",
        "39",
        "40",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "49",
        "53",
        "55",
        "65",
        "66",
        "67"
    ),
    "482" to arrayOf(
        "2",
        "30",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "37",
        "38",
        "39",
        "42",
        "44",
        "46",
        "49",
        "50",
        "51",
        "53",
        "55",
        "57",
        "58",
        "61",
        "62",
        "63",
        "64",
        "65",
        "66",
        "67",
        "68",
        "69",
        "71",
        "72",
        "73",
        "74",
        "75",
        "76"
    ),
    "483" to arrayOf(
        "2",
        "30",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "38",
        "39",
        "40",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "49",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "58"
    ),
    "484" to arrayOf(
        "2",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "37",
        "38",
        "39",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "49",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57",
        "58"
    ),
    "485" to arrayOf(
        "2",
        "3",
        "31",
        "32",
        "33",
        "34",
        "35",
        "38",
        "39",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "49",
        "5"
    ),
    "486" to arrayOf(
        "2",
        "40",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "49",
        "62",
        "63",
        "64",
        "65",
        "66",
        "67",
        "72",
        "73",
        "74",
        "75",
        "76",
        "77",
        "78",
        "79"
    ),
    "487" to arrayOf(
        "2",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "61",
        "62",
        "63",
        "66",
        "67",
        "68"
    ),
    "491" to arrayOf(
        "2",
        "30",
        "31",
        "32",
        "33",
        "35",
        "36",
        "37",
        "38",
        "39",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57",
        "58",
        "64"
    ),
    "492" to arrayOf(
        "2",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "37",
        "38",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "54"
    ),
    "493" to arrayOf(
        "2",
        "31",
        "33",
        "34",
        "36",
        "37",
        "39",
        "41",
        "43",
        "44",
        "45",
        "46",
        "47",
        "49",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57"
    ),
    "494" to arrayOf(
        "2",
        "30",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "37",
        "38",
        "39",
        "40",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "49",
        "50",
        "51",
        "52",
        "53"
    ),
    "495" to arrayOf(""),
    "496" to arrayOf(
        "20",
        "21",
        "22",
        "24",
        "25",
        "26",
        "27",
        "28",
        "29",
        "30",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "37",
        "38",
        "39",
        "40",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "49",
        "5",
        "50",
        "51",
        "52",
        "53",
        "54",
        "57",
        "6",
        "61",
        "63",
        "64",
        "65",
        "66",
        "67",
        "69",
        "7",
        "70",
        "71",
        "72",
        "73",
        "74",
        "75",
        "76",
        "77",
        "9"
    ),
    "811" to arrayOf(
        "2",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "37",
        "38",
        "39",
        "40",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "49",
        "50",
        "51",
        "52",
        "53",
        "9"
    ),
    "812" to arrayOf(""),
    "813" to arrayOf(
        "4",
        "45",
        "5",
        "56",
        "60",
        "61",
        "62",
        "63",
        "64",
        "65",
        "66",
        "67",
        "68",
        "69",
        "70",
        "71",
        "72",
        "73",
        "74",
        "75",
        "76",
        "77",
        "78",
        "79",
        "94"
    ),
    "814" to arrayOf(
        "2",
        "30",
        "31",
        "33",
        "34",
        "36",
        "37",
        "39",
        "50",
        "51",
        "52",
        "54",
        "55",
        "56",
        "57",
        "58",
        "59"
    ),
    "815" to arrayOf(
        "2",
        "3",
        "30",
        "31",
        "32",
        "33",
        "35",
        "36",
        "38",
        "39",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "58",
        "59",
        "9"
    ),
    "816" to arrayOf(
        "2",
        "29",
        "50",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57",
        "58",
        "59",
        "60",
        "61",
        "62",
        "63",
        "64",
        "65",
        "66",
        "67",
        "68",
        "69"
    ),
    "817" to arrayOf(
        "2",
        "32",
        "33",
        "37",
        "38",
        "39",
        "40",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "49",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57",
        "58",
        "59"
    ),
    "818" to arrayOf(
        "2",
        "30",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "37",
        "38",
        "39",
        "4",
        "48",
        "50",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57",
        "58",
        "59",
        "70"
    ),
    "820" to arrayOf("2"),
    "821" to arrayOf(
        "2",
        "30",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "37",
        "38",
        "39",
        "4",
        "40",
        "41",
        "42",
        "44",
        "45",
        "46",
        "49",
        "51",
        "6"
    ),
    "831" to arrayOf(
        "3",
        "30",
        "34",
        "36",
        "37",
        "38",
        "39",
        "40",
        "44",
        "47",
        "48",
        "50",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57",
        "58",
        "59",
        "60",
        "61",
        "62",
        "63",
        "64",
        "65",
        "66",
        "67",
        "68",
        "69",
        "70",
        "71",
        "72",
        "73",
        "74",
        "75",
        "76",
        "77",
        "78",
        "79",
        "90",
        "91",
        "92",
        "93",
        "94",
        "95",
        "96",
        "97"
    ),
    "833" to arrayOf(
        "2",
        "30",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "37",
        "38",
        "39",
        "40",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "49",
        "50",
        "51",
        "52",
        "53",
        "54",
        "55",
        "57",
        "58",
        "59",
        "61",
        "62",
        "63",
        "64",
        "65",
        "66",
        "67",
        "68",
        "69",
        "75"
    ),
    "834" to arrayOf(
        "2",
        "31",
        "32",
        "33",
        "34",
        "36",
        "37",
        "38",
        "39",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "49",
        "51",
        "53",
        "54",
        "56",
        "57",
        "58"
    ),
    "835" to arrayOf(
        "2",
        "27",
        "30",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "37",
        "38",
        "39",
        "40",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "49",
        "51"
    ),
    "836" to arrayOf(
        "2",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "37",
        "38",
        "39",
        "41",
        "43",
        "44",
        "45"
    ),
    "841" to arrayOf(
        "2",
        "40",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "49",
        "50",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57",
        "58",
        "59",
        "6",
        "62",
        "64",
        "65",
        "67",
        "68",
        "69"
    ),
    "842" to arrayOf(
        "2",
        "30",
        "31",
        "32",
        "33",
        "34",
        "35",
        "37",
        "38",
        "39",
        "40",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "49",
        "53",
        "54",
        "55"
    ),
    "843" to arrayOf(
        "41",
        "42",
        "44",
        "45",
        "46",
        "47",
        "48",
        "57",
        "60",
        "61",
        "62",
        "64",
        "65",
        "66",
        "67",
        "68",
        "69",
        "70",
        "71",
        "73",
        "74",
        "75",
        "76",
        "77",
        "78",
        "79",
        "96"
    ),
    "844" to arrayOf(
        "2",
        "3",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57",
        "58",
        "61",
        "62",
        "63",
        "64",
        "65",
        "66",
        "67",
        "68",
        "72",
        "73",
        "74",
        "75",
        "76",
        "77",
        "78",
        "79",
        "9",
        "92",
        "93",
        "94",
        "95"
    ),
    "845" to arrayOf(
        "3",
        "40",
        "42",
        "43",
        "44",
        "45",
        "48",
        "49",
        "50",
        "51",
        "52",
        "54",
        "55",
        "57",
        "58",
        "60",
        "61",
        "62",
        "63",
        "64",
        "65",
        "66",
        "67",
        "68",
        "73",
        "74",
        "75",
        "76",
        "77",
        "78",
        "79",
        "91",
        "92",
        "93",
        "95",
        "96"
    ),
    "846" to arrayOf(
        "35",
        "39",
        "4",
        "46",
        "47",
        "48",
        "50",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57",
        "58",
        "60",
        "61",
        "62",
        "63",
        "64",
        "66",
        "67",
        "70",
        "71",
        "72",
        "73",
        "74",
        "75",
        "76",
        "77"
    ),
    "847" to arrayOf(
        "22",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47"
    ),
    "848" to arrayOf("24", "27", "62"),
    "851" to arrayOf("2", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "71", "72"),
    "855" to arrayOf(
        "3",
        "49",
        "5",
        "51",
        "55",
        "56",
        "57",
        "58",
        "59",
        "63",
        "69",
        "92",
        "93",
        "94",
        "95",
        "99"
    ),
    "861" to arrayOf(
        "30",
        "31",
        "32",
        "33",
        "35",
        "37",
        "38",
        "40",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "49",
        "50",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57",
        "58",
        "59",
        "60",
        "61",
        "62",
        "63",
        "64",
        "65",
        "66",
        "67",
        "68",
        "69",
        "7",
        "91",
        "92",
        "93"
    ),
    "862" to arrayOf("", "2"),
    "863" to arrayOf(
        "14",
        "17",
        "18",
        "4",
        "40",
        "41",
        "42",
        "47",
        "48",
        "49",
        "50",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57",
        "58",
        "59",
        "6",
        "61",
        "63",
        "64",
        "65",
        "67",
        "68",
        "69",
        "70",
        "71",
        "72",
        "73",
        "74",
        "75",
        "76",
        "77",
        "78",
        "79",
        "82",
        "83",
        "84",
        "85",
        "86",
        "87",
        "88",
        "89",
        "91",
        "92",
        "93",
        "94",
        "95",
        "96",
        "97"
    ),
    "865" to arrayOf(
        "2",
        "40",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "49",
        "50",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57",
        "58",
        "59",
        "60",
        "63",
        "65"
    ),
    "866" to arrayOf("2", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39"),
    "867" to arrayOf("2", "3", "31", "32", "33", "34", "35", "36", "37"),
    "869" to arrayOf("2"),
    "871" to arrayOf(
        "2",
        "32",
        "34",
        "35",
        "36",
        "42",
        "43",
        "45",
        "46",
        "47",
        "48",
        "52",
        "55",
        "56",
        "64"
    ),
    "872" to arrayOf(
        "2",
        "30",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "37",
        "38",
        "39",
        "40",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "49",
        "50",
        "52",
        "54",
        "55",
        "56",
        "57",
        "58",
        "59",
        "60",
        "61",
        "62",
        "63",
        "64",
        "65",
        "66",
        "67",
        "68",
        "69",
        "71",
        "72",
        "73",
        "74",
        "75",
        "76",
        "78",
        "79"
    ),
    "873" to arrayOf("22", "43", "44", "45", "46", "47"),
    "877" to arrayOf("2", "70", "71", "72", "73", "77", "78", "79"),
    "878" to arrayOf("22", "70", "73", "74", "75", "76", "77", "78", "79"),
    "879" to arrayOf("2", "3", "34", "35", "37", "38", "51", "61", "64")
)

/**
 * Список возможных телефонных кодов стран.
 */
internal val FOREIGN_CODES = mapOf(
    "1" to arrayOf(),
    "2" to arrayOf(
        "0",
        "11",
        "12",
        "13",
        "16",
        "18",
        "20",
        "21",
        "22",
        "23",
        "24",
        "25",
        "26",
        "27",
        "28",
        "29",
        "30",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "37",
        "38",
        "39",
        "40",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "49",
        "50",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57",
        "58",
        "60",
        "61",
        "62",
        "63",
        "64",
        "65",
        "66",
        "67",
        "68",
        "69",
        "7",
        "90",
        "91",
        "97",
        "98",
        "99"
    ),
    "3" to arrayOf(
        "0",
        "1",
        "2",
        "3",
        "4",
        "50",
        "51",
        "52",
        "53",
        "54",
        "55",
        "56",
        "57",
        "58",
        "59",
        "6",
        "70",
        "71",
        "72",
        "73",
        "74",
        "75",
        "76",
        "77",
        "78",
        "79",
        "80",
        "81",
        "82",
        "83",
        "85",
        "86",
        "87",
        "88",
        "89",
        "9"
    ),
    "4" to arrayOf("0", "1", "20", "21", "23", "3", "4", "5", "6", "7", "8", "9"),
    "5" to arrayOf(
        "00",
        "01",
        "02",
        "03",
        "04",
        "05",
        "06",
        "07",
        "08",
        "09",
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
        "90",
        "91",
        "92",
        "93",
        "94",
        "95",
        "96",
        "97",
        "98",
        "99"
    ),
    "6" to arrayOf(
        "0",
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "70",
        "72",
        "73",
        "74",
        "75",
        "76",
        "77",
        "78",
        "79",
        "80",
        "81",
        "82",
        "83",
        "85",
        "86",
        "87",
        "88",
        "89",
        "90",
        "91",
        "92"
    ),
    "8" to arrayOf(
        "00",
        "08",
        "1",
        "2",
        "4",
        "50",
        "52",
        "53",
        "55",
        "56",
        "6",
        "70",
        "78",
        "80",
        "81",
        "82",
        "83",
        "86",
        "88"
    ),
    "9" to arrayOf(
        "0",
        "1",
        "2",
        "3",
        "4",
        "5",
        "60",
        "61",
        "62",
        "63",
        "64",
        "65",
        "66",
        "67",
        "68",
        "70",
        "71",
        "72",
        "73",
        "74",
        "75",
        "76",
        "77",
        "79",
        "8",
        "91",
        "92",
        "93",
        "94",
        "95",
        "96",
        "98"
    )
)