# JUnit
jar_library(name='junit', jars=[
  jar(org='junit', name='junit', rev='4.12'),
  jar(org='org.pantsbuild', name='junit-runner-annotations', rev='0.0.11')
])

# Scalatest
jar_library(name='scalatest',
  jars=[jar(org='org.scalatest', name='scalatest_2.11', rev='3.0.0', excludes=[exclude('org.scala-lang')])],
  dependencies=['//:scala-library', '//:scala-reflect']
)

# Mockito
jar_library(name='mockito', jars=[
  jar(org='org.mockito', name='mockito-all', rev='1.10.19')
])
