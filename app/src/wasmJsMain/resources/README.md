# SingularityUniverse

**Singularity Web App Source Code**: https://singularity-universe.com

A comprehensive Kotlin Multiplatform ecosystem built with Compose Multiplatform, targeting WebAssembly (WASM) for modern web applications.

## 🚀 Project Overview

SingularityUniverse is a multi-application ecosystem featuring three distinct applications:
- **Console**: Terminal-like interface application
- **Desktop**: Desktop-style application interface
- **AppSketch**: Application prototyping and sketching tool

All applications are built using Kotlin Multiplatform and Compose Multiplatform, compiled to WebAssembly for optimal web performance.

## 🏗️ Architecture

This project uses a modular architecture with:
- **Root project**: Core configuration and build orchestration
- **app**: Main application module with WASM compilation
- **application/**: Individual application modules
    - `Console/`: Terminal interface application
    - `Desktop/`: Desktop-style application
    - `AppSketch/`: Application sketching tool
- **subproject/**: Compiled application artifacts

## 🛠️ Technology Stack

- **Language**: Kotlin 2.1.10
- **UI Framework**: Compose Multiplatform 1.7.3
- **Target Platform**: WebAssembly (WASM)
- **Build System**: Gradle with Kotlin DSL
- **Architecture**: Kotlin Multiplatform
- **Lifecycle**: AndroidX Lifecycle 2.8.4

## 🔧 Development

### Project Structure
```
SingularityUniverse/
├── app/                          # Main WASM application
│   ├── src/wasmJsMain/          # WASM-specific source code
│   └── build.gradle.kts         # Main app configuration
├── application/                  # Individual applications
│   ├── Console/                 # Console application
│   ├── Desktop/                 # Desktop application
│   └── AppSketch/               # App sketching tool
├── script/                      # Build and deployment scripts
│   ├── compile-subproject       # Subproject compilation script
│   └── release                  # Release script
├── gradle/                      # Gradle configuration
│   └── libs.versions.toml       # Dependency versions
└── subproject/                  # Compiled application outputs
```

### Adding New Applications

1. Create a new directory under `application/`
2. Set up Kotlin Multiplatform configuration
3. Add to `settings.gradle.kts`:
   ```kotlin
   includeBuild("application/YourNewApp")
   ```
4. Update the compilation script if needed

### WASM Considerations
- Ensure your web server supports serving WebAssembly files
- Modern browsers with WASM support are required
- Enable cross-origin isolation headers for optimal performance:
  ```
  Cross-Origin-Embedder-Policy: require-corp
  Cross-Origin-Opener-Policy: same-origin
  ```

## 🔍 Browser Compatibility

| Browser | Version | Support |
|---------|---------|---------|
| Chrome  | 87+     | ✅ Full |
| Firefox | 79+     | ✅ Full |
| Safari  | 14+     | ✅ Full |
| Edge    | 87+     | ✅ Full |

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Make your changes following the existing code style
4. Add tests for new functionality
5. Commit your changes: `git commit -m 'Add amazing feature'`
6. Push to the branch: `git push origin feature/amazing-feature`
7. Open a Pull Request

## 📄 License

This project is licensed under the GNU Lesser General Public License v2.1 (LGPL-2.1).

```
Copyright (C) 2025 singularity-universe.com
Jln. Raya Saren No.50. Sleman Yogyakarta Indonesia
```

See [LICENSE.txt](LICENSE.txt) for the full license text.

### License Summary
- ✅ **Commercial use** allowed
- ✅ **Modification** allowed
- ✅ **Distribution** allowed
- ✅ **Private use** allowed
- ❗ **Patent grant** not provided
- ❗ **Trademark rights** not granted
- ⚠️ **Liability** limitations apply
- ⚠️ **Warranty** not provided

## 👨‍💻 Author

**Stefanus Ayudha**
- Email: [stefanus.ayudha@gmail.com](mailto:stefanus.ayudha@gmail.com)
- Website: [singularity-universe.com](https://singularity-universe.com)

## 🆘 Support

If you encounter any issues or have questions, report on the main repository page:
https://github.com/SingularityIndonesia/SingularityWebAppSrc

## 📈 Roadmap

- [ ] Add more application modules
- [ ] Implement shared component library
- [ ] Add comprehensive testing suite
- [ ] Performance optimizations
- [ ] Mobile-responsive improvements
- [ ] CI/CD pipeline setup

---

*Built with ❤️ using Kotlin Multiplatform and Compose Multiplatform*