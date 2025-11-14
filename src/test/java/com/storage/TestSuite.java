package com.storage;

import org.junit.platform.suite.api.*;

@Suite
@SuiteDisplayName("Storage Management System Test Suite")
@SelectPackages("com.storage")
@IncludeClassNamePatterns(".*Test")
public class TestSuite {
}