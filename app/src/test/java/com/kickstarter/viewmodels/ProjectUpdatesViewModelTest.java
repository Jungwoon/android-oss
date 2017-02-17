package com.kickstarter.viewmodels;

import android.content.Intent;
import android.util.Pair;

import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.factories.ProjectFactory;
import com.kickstarter.models.Project;
import com.kickstarter.models.Update;
import com.kickstarter.ui.IntentKey;

import org.junit.Test;

import okhttp3.Request;
import rx.observers.TestSubscriber;

public final class ProjectUpdatesViewModelTest extends KSRobolectricTestCase {

  @Test
  public void testProjectUpdatesViewModel_ExternalLinkActivated() {
    final ProjectUpdatesViewModel.ViewModel vm = new ProjectUpdatesViewModel.ViewModel(environment());
    final Project project = ProjectFactory.project();

    final TestSubscriber<String> webViewUrl = new TestSubscriber<>();
    vm.outputs.webViewUrl().subscribe(webViewUrl);

    vm.intent(new Intent().putExtra(IntentKey.PROJECT, project));

    // Initial updates index url is loaded.
    webViewUrl.assertValueCount(1);

    // Activate an external link.
    vm.inputs.externalLinkActivated();

    // External url is not loaded in our web view.
    webViewUrl.assertValueCount(1);
    koalaTest.assertValues("Viewed Updates", "Opened External Link");
  }

  @Test
  public void testProjectUpdatesViewModel_LoadsInitialIndexUrl() {
    final ProjectUpdatesViewModel.ViewModel vm = new ProjectUpdatesViewModel.ViewModel(environment());
    final Project project = ProjectFactory.project();

    final TestSubscriber<String> initialIndexUrl = new TestSubscriber<>();
    vm.outputs.webViewUrl().subscribe(initialIndexUrl);

    vm.intent(new Intent().putExtra(IntentKey.PROJECT, project));
    initialIndexUrl.assertValues(project.updatesUrl());
    koalaTest.assertValues("Viewed Updates");
  }

  @Test
  public void testProjectUpdatesViewModel_StartCommentsActivity() {
    final ProjectUpdatesViewModel.ViewModel vm = new ProjectUpdatesViewModel.ViewModel(environment());
    final Project project = ProjectFactory.project();

    final Request commentsRequest = new Request.Builder()
      .url("https://kck.str/projects/param/param/posts/id/comments")
      .build();

    final TestSubscriber<Update> startCommentsActivity = new TestSubscriber<>();
    vm.outputs.startCommentsActivity().subscribe(startCommentsActivity);

    vm.intent(new Intent().putExtra(IntentKey.PROJECT, project));
    vm.inputs.goToCommentsRequest(commentsRequest);

    startCommentsActivity.assertValueCount(1);
  }

  @Test
  public void testProjectUpdatesViewModel_StartUpdateActivity() {
    final ProjectUpdatesViewModel.ViewModel vm = new ProjectUpdatesViewModel.ViewModel(environment());
    final Project project = ProjectFactory.project();

    final Request updateRequest = new Request.Builder()
      .url("https://kck.str/projects/param/param/posts/id")
      .build();

    final TestSubscriber<Pair<Project, Update>> startUpdateActivity = new TestSubscriber<>();
    vm.outputs.startUpdateActivity().subscribe(startUpdateActivity);

    vm.intent(new Intent().putExtra(IntentKey.PROJECT, project));
    vm.inputs.goToUpdateRequest(updateRequest);

    startUpdateActivity.assertValueCount(1);
  }
}