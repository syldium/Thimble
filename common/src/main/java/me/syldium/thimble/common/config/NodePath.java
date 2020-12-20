package me.syldium.thimble.common.config;

import org.intellij.lang.annotations.Pattern;

/**
 * This annotation may be used when a config path is excepted.
 */
@Pattern("[a-zA-Z0-9-]+")
public @interface NodePath {
}
