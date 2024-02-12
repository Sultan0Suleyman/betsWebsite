import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-photo-slider',
  templateUrl: './photo-slider.component.html',
  styleUrls: ['./photo-slider.component.css']
})
export class PhotoSliderComponent implements OnInit {
  photos: string[] = [
    "assets/ContentPhotos/img_1.png",
    "assets/ContentPhotos/img_2.png",
    "assets/ContentPhotos/img_3.png",
    "assets/ContentPhotos/img_4.png",
    "assets/ContentPhotos/img_5.png"
    // Добавьте другие URL-адреса фотографий
  ];
  currentPhotoIndex: number = 0;

  ngOnInit() {
    // Запустите интервал для автоматического переключения фотографий каждые 10 секунд
    setInterval(() => {
      this.showNextPhoto();
      const button = document.querySelector(".newsChanger")
      if(button)button.classList.remove("visibleButton")//кнопка исчезает при авто смене
    }, 10000);
  }

  showNextPhoto() {
    this.currentPhotoIndex = (this.currentPhotoIndex + 1) % this.photos.length;//% для того, чтобы индекс уже
    // последней фотки автоматически менялся на 0
  }
  onPanelMouseEnter() {
    const button = document.querySelector(".newsChanger")
    if(button){
      button.classList.add("visibleButton")
    }
  }

  onClickButton(){
    this.currentPhotoIndex = (this.currentPhotoIndex + 1) % this.photos.length;//% для того, чтобы индекс уже
    // последней фотки автоматически менялся на 0
  }


}
