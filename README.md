
# react-native-nearby

## Getting started

`$ npm install react-native-nearby --save`

### iOS

1. Initiate a podfile in case you don't have a workspace yet `cd ios/ && pod init`
2. Add `pod 'NearbyMessages'` to the podfile located at `Podfile`
3. Run `pod install` to install the dependencies

### Mostly automatic installation

`$ react-native link react-native-nearby`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-nearby` and add `RNNearby.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNNearby.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNNearbyPackage;` to the imports at the top of the file
  - Add `new RNNearbyPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-nearby'
  	project(':react-native-nearby').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-nearby/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-nearby')
  	```

#### Windows
[Read it! :D](https://github.com/ReactWindows/react-native)

1. In Visual Studio add the `RNNearby.sln` in `node_modules/react-native-nearby/windows/RNNearby.sln` folder to their solution, reference from their app.
2. Open up your `MainPage.cs` app
  - Add `using Nearby.RNNearby;` to the usings at the top of the file
  - Add `new RNNearbyPackage()` to the `List<IReactPackage>` returned by the `Packages` method


## Usage
```javascript
import RNNearby from 'react-native-nearby';

// TODO: What to do with the module?
RNNearby;
```
  