package de.uni_passau.fim.talent_tauscher.st;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/** The test suite used for standard load tests. */
@Suite
@SelectClasses({
  OwnAdvertisementsST.class,
})
public class StandardTestSuite {}
