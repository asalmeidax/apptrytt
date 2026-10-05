# Brazuca Lite Demo

Projeto Android mínimo para catálogo remoto + cache local + player Media3.

## O que já funciona
- abre instantaneamente usando catálogo embutido ou último cache salvo;
- pesquisa por título/categoria;
- grade simples com capas;
- reprodução MP4/HLS pelo Media3/ExoPlayer;
- botão **Fonte** para informar a URL HTTPS do `config.json`;
- botão **Atualizar**;
- atualização por versão (`catalogVersion`): só baixa `catalog.json` quando a versão remota é maior;
- em falha de rede, conserva o catálogo local.

## Formato do config.json
```json
{
  "catalogVersion": 2,
  "minimumAppVersion": 1,
  "catalogUrl": "https://seu-dominio/catalog.json"
}
```

## Formato do catalog.json
```json
{
  "items": [
    {
      "id": "abc",
      "title": "Meu vídeo",
      "year": "2026",
      "category": "Filme",
      "poster": "https://.../poster.jpg",
      "streamUrl": "https://.../master.m3u8"
    }
  ]
}
```

Use apenas streams e catálogos que você tenha autorização para acessar/distribuir.

## Gerar APK no Android Studio
1. Instale uma versão atual do Android Studio com JDK 17 e Android SDK 36.
2. Abra esta pasta como projeto.
3. Aguarde o Gradle Sync terminar.
4. Se o Android Studio pedir SDK 36, instale em **SDK Manager**.
5. No menu, use **Build > Build App Bundle(s) / APK(s) > Build APK(s)**.
6. O APK de debug ficará em `app/build/outputs/apk/debug/app-debug.apk`.

## Ativar atualização online com GitHub Pages
1. Crie um repositório público, por exemplo `brazuca-lite`.
2. Envie os dois arquivos da pasta `remote-example`: `config.json` e `catalog.json`.
3. Em GitHub: Settings > Pages > Deploy from a branch > `main` / root.
4. Edite `config.json` para que `catalogUrl` aponte para seu `catalog.json` publicado.
5. No app, toque **Fonte** e cole a URL do `config.json`.
6. Para atualizar sem reinstalar APK: altere `catalog.json` e aumente `catalogVersion` em `config.json`.

Exemplo: versão 1 -> versão 2. O app detecta a mudança e baixa o novo catálogo.

## Observação
A atualização remota deste projeto atualiza **dados/listas**, não código executável. Mudanças de código exigem novo APK.

## Gerar APK sem instalar Android Studio (GitHub Actions)
1. Crie um repositório no GitHub.
2. Envie todo o conteúdo desta pasta para a branch `main`.
3. Abra a aba **Actions** do repositório.
4. Entre em **Build Android APK** e clique **Run workflow** (ou apenas faça um push para `main`).
5. Quando terminar, abra a execução e baixe o artefato **BrazucaLiteDemo-debug**.
6. Dentro do ZIP do artefato estará `app-debug.apk`.

O workflow usa Java 17, Gradle 9.4.1 e o Android Gradle Plugin 9.2.0.

## Mi Stick / Android TV
Esta versão inclui suporte a controle remoto (D-pad) e launcher Android TV.
- Setas: navegar pelo catálogo
- OK: abrir título / play-pause no player
- Esquerda/Direita no player: -10s / +10s
- Voltar: sair do player
- Campo Pesquisar: OK abre o teclado virtual da TV

O APK continua instalável por sideload na Mi Stick. A categoria LEANBACK_LAUNCHER faz o app aparecer como aplicativo de TV em launchers compatíveis.
