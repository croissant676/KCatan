/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan

fun catan(name: String) = "Catan > $name"

fun <V> Map<*, V>.invoke(): Collection<V> = this.values