import { Component } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { MatTableDataSource } from '@angular/material';
import { WikiSearchModel } from './wiki-search.model';
import { WikiSearchService } from './wiki-search.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'AID Wikipedia Search';
  displayedColumns: string[] = ['Wikipedia Id', 'Contributor', 'Article'];
  wikiSearchList = new MatTableDataSource<WikiSearchModel[]>();
  selected = "10"; // default size

  searchForm: FormGroup;

  constructor (private formBuilder: FormBuilder, private wikiSearchService: WikiSearchService) { }

  ngOnInit() {
    this.searchForm = this.formBuilder.group({
      search: ['', Validators.required],
    });
  }

  onSearch() {
    if (!this.searchForm.valid) return;
    this.wikiSearchService.getWikiArticles(this.searchForm.get('search').value, this.selected).subscribe((response) => {
      this.wikiSearchList = response as any;
    }, error => {

    });

  }
}
